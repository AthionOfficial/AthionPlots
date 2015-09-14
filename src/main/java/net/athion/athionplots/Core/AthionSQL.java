package net.athion.athionplots.Core;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Utils.UUIDFetcher;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class AthionSQL {

    public final static String sqlitedb = "/plots.db";
    private final static String PLOT_TABLE = "CREATE TABLE `plotmePlots` ("
    + "`idX` INTEGER,"
    + "`idZ` INTEGER,"
    + "`owner` varchar(100) NOT NULL,"
    + "`world` varchar(32) NOT NULL DEFAULT '0',"
    + "`topX` INTEGER NOT NULL DEFAULT '0',"
    + "`bottomX` INTEGER NOT NULL DEFAULT '0',"
    + "`topZ` INTEGER NOT NULL DEFAULT '0',"
    + "`bottomZ` INTEGER NOT NULL DEFAULT '0',"
    + "`biome` varchar(32) NOT NULL DEFAULT '0',"
    + "`expireddate` DATETIME NULL,"
    + "`finished` boolean NOT NULL DEFAULT '0',"
    + "`customprice` double NOT NULL DEFAULT '0',"
    + "`forsale` boolean NOT NULL DEFAULT '0',"
    + "`finisheddate` varchar(16) NULL,"
    + "`protected` boolean NOT NULL DEFAULT '0',"
    + "`auctionned` boolean NOT NULL DEFAULT '0',"
    + "`auctionenddate` varchar(16) NULL,"
    + "`currentbid` double NOT NULL DEFAULT '0',"
    + "`currentbidder` varchar(32) NULL,"
    + "`currentbidderId` blob(16),"
    + "`ownerId` blob(16),"
    + "PRIMARY KEY (idX, idZ, world));";
    private final static String COMMENT_TABLE = "CREATE TABLE `plotmeComments` ("
    + "`idX` INTEGER,"
    + "`idZ` INTEGER,"
    + "`world` varchar(32) NOT NULL,"
    + "`commentid` INTEGER,"
    + "`player` varchar(32) NOT NULL,"
    + "`comment` text,"
    + "`playerid` blob(16),"
    + "PRIMARY KEY (idX, idZ, world, commentid));";
    private final static String ALLOWED_TABLE = "CREATE TABLE `plotmeAllowed` ("
    + "`idX` INTEGER,"
    + "`idZ` INTEGER,"
    + "`world` varchar(32) NOT NULL,"
    + "`player` varchar(32) NOT NULL,"
    + "`playerid` blob(16),"
    + "PRIMARY KEY (idX, idZ, world, player));";
    private final static String DENIED_TABLE = "CREATE TABLE `plotmeDenied` ("
    + "`idX` INTEGER,"
    + "`idZ` INTEGER,"
    + "`world` varchar(32) NOT NULL,"
    + "`player` varchar(32) NOT NULL,"
    + "`playerid` blob(16),"
    + "PRIMARY KEY (idX, idZ, world, player));";
    private static Connection conn = null;

    private static boolean tableExists(final String name) {
        ResultSet rs = null;
        try {
            final Connection conn = getConnection();

            final DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, name, null);
            if (!rs.next()) {
                return false;
            }
            return true;
        } catch (final SQLException ex) {
            AthionPlots.logger.severe("Table Check Exception :");
            AthionPlots.logger.severe("  " + ex.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Table Check SQL Exception (on closing) :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
    }

    private static void createTable() {
        Statement st = null;
        try {
            final Connection conn = getConnection();
            st = conn.createStatement();

            if (!tableExists("plotmePlots")) {
                st.executeUpdate(PLOT_TABLE);
                conn.commit();
            }

            if (!tableExists("plotmeComments")) {
                st.executeUpdate(COMMENT_TABLE);
                conn.commit();
            }

            if (!tableExists("plotmeAllowed")) {
                st.executeUpdate(ALLOWED_TABLE);
                conn.commit();
            }

            if (!tableExists("plotmeDenied")) {
                st.executeUpdate(DENIED_TABLE);
                conn.commit();
            }

            UpdateTables();

            if (AthionPlots.usemySQL) {
                final File sqlitefile = new File(AthionPlots.configpath + sqlitedb);
                if (!sqlitefile.exists()) {
                    return;
                } else {
                    AthionPlots.logger.info("Modifying database for MySQL support");
                    AthionPlots.logger.info("Trying to import plots from plots.db");
                    Class.forName("org.sqlite.JDBC");
                    final Connection sqliteconn = DriverManager.getConnection("jdbc:sqlite:" + AthionPlots.configpath + sqlitedb);
                    sqliteconn.setAutoCommit(false);
                    final Statement slstatement = sqliteconn.createStatement();
                    final ResultSet setPlots = slstatement.executeQuery("SELECT * FROM plotmePlots");
                    final Statement slAllowed = sqliteconn.createStatement();
                    ResultSet setAllowed = null;
                    final Statement slDenied = sqliteconn.createStatement();
                    ResultSet setDenied = null;
                    final Statement slComments = sqliteconn.createStatement();
                    ResultSet setComments = null;

                    int size = 0;
                    while (setPlots.next()) {
                        final int idX = setPlots.getInt("idX");
                        final int idZ = setPlots.getInt("idZ");
                        final String owner = setPlots.getString("owner");
                        final String world = setPlots.getString("world").toLowerCase();
                        final int topX = setPlots.getInt("topX");
                        final int bottomX = setPlots.getInt("bottomX");
                        final int topZ = setPlots.getInt("topZ");
                        final int bottomZ = setPlots.getInt("bottomZ");
                        final String biome = setPlots.getString("biome");
                        final java.sql.Date expireddate = setPlots.getDate("expireddate");
                        final boolean finished = setPlots.getBoolean("finished");
                        final AthionPlayers allowed = new AthionPlayers();
                        final AthionPlayers denied = new AthionPlayers();
                        final List<String[]> comments = new ArrayList<String[]>();
                        final double customprice = setPlots.getDouble("customprice");
                        final boolean forsale = setPlots.getBoolean("forsale");
                        final String finisheddate = setPlots.getString("finisheddate");
                        final boolean protect = setPlots.getBoolean("protected");
                        final boolean auctionned = setPlots.getBoolean("auctionned");
                        final String currentbidder = setPlots.getString("currentbidder");
                        final double currentbid = setPlots.getDouble("currentbid");

                        final byte[] byOwner = setPlots.getBytes("ownerId");
                        final byte[] byBidder = setPlots.getBytes("currentbidderid");

                        UUID ownerId = null;
                        UUID currentbidderid = null;

                        if (byOwner != null) {
                            ownerId = UUIDFetcher.fromBytes(byOwner);
                        }
                        if (byBidder != null) {
                            currentbidderid = UUIDFetcher.fromBytes(byBidder);
                        }

                        setAllowed = slAllowed.executeQuery("SELECT * FROM plotmeAllowed WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND world = '" + world + "'");

                        while (setAllowed.next()) {
                            final byte[] byPlayerId = setAllowed.getBytes("playerid");
                            if (byPlayerId == null) {
                                allowed.put(setAllowed.getString("player"));
                            } else {
                                allowed.put(setAllowed.getString("player"), UUIDFetcher.fromBytes(byPlayerId));
                            }
                        }

                        if (setAllowed != null) {
                            setAllowed.close();
                        }

                        setDenied = slDenied.executeQuery("SELECT * FROM plotmeDenied WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND world = '" + world + "'");

                        while (setDenied.next()) {
                            final byte[] byPlayerId = setDenied.getBytes("playerid");
                            if (byPlayerId == null) {
                                denied.put(setDenied.getString("player"));
                            } else {
                                denied.put(setDenied.getString("player"), UUIDFetcher.fromBytes(byPlayerId));
                            }
                        }

                        if (setDenied != null) {
                            setDenied.close();
                        }

                        setComments = slComments.executeQuery("SELECT * FROM plotmeComments WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND world = '" + world + "'");

                        while (setComments.next()) {
                            final String[] comment = new String[3];

                            final byte[] byPlayerId = setComments.getBytes("playerid");
                            if (byPlayerId != null) {
                                comment[2] = UUIDFetcher.fromBytes(byPlayerId).toString();
                            } else {
                                comment[2] = null;
                            }

                            comment[0] = setComments.getString("player");
                            comment[1] = setComments.getString("comment");
                            comments.add(comment);
                        }

                        final AthionPlot plot = new AthionPlot(owner, ownerId, world, topX, bottomX, topZ, bottomZ, biome, expireddate, finished, allowed, comments, "" + idX + ";" + idZ, customprice,
                        forsale, finisheddate, protect, currentbidder, currentbidderid, currentbid, auctionned, denied);
                        addPlot(plot, idX, idZ, topX, bottomX, topZ, bottomZ);

                        size++;
                    }
                    AthionPlots.logger.info("Imported " + size + " plots from " + sqlitedb);
                    if (slstatement != null) {
                        slstatement.close();
                    }
                    if (slAllowed != null) {
                        slAllowed.close();
                    }
                    if (slComments != null) {
                        slComments.close();
                    }
                    if (slDenied != null) {
                        slDenied.close();
                    }
                    if (setPlots != null) {
                        setPlots.close();
                    }
                    if (setComments != null) {
                        setComments.close();
                    }
                    if (setAllowed != null) {
                        setAllowed.close();
                    }
                    if (sqliteconn != null) {
                        sqliteconn.close();
                    }

                    AthionPlots.logger.info("Renaming " + sqlitedb + " to " + sqlitedb + ".old");
                    if (!sqlitefile.renameTo(new File(AthionPlots.configpath, sqlitedb + ".old"))) {
                        AthionPlots.logger.warning("Failed to rename " + sqlitedb + "! Please rename this manually!");
                    }
                }
            }
        } catch (final SQLException ex) {
            AthionPlots.logger.severe("Create Table Exception :");
            //AthionPlots.logger.severe("  " + ex.getMessage());
            ex.printStackTrace();
        } catch (final ClassNotFoundException ex) {
            AthionPlots.logger.severe("You need the SQLite library :");
            AthionPlots.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Could not create the table (on close) :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
    }

    public static String getSchema() {
        final String conn = AthionPlots.mySQLconn;

        if (conn.lastIndexOf("/") > 0) {
            return conn.substring(conn.lastIndexOf("/") + 1);
        } else {
            return "";
        }
    }

    public static void UpdateTables() {
        Statement statement = null;
        ResultSet set = null;

        try {
            final Connection conn = getConnection();

            statement = conn.createStatement();

            final String schema = getSchema();

            if (AthionPlots.usemySQL) {
                /*** START Version 0.8 changes ***/
                // CustomPrice
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " + "TABLE_NAME='plotmePlots' AND column_name='customprice'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD customprice double NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();

                // ForSale
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " + "TABLE_NAME='plotmePlots' AND column_name='forsale'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD forsale boolean NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();

                // finisheddate
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " + "TABLE_NAME='plotmePlots' AND column_name='finisheddate'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD finisheddate varchar(16) NULL;");
                    conn.commit();
                }
                set.close();

                // Protected
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " + "TABLE_NAME='plotmePlots' AND column_name='protected'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD protected boolean NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();

                // Auctionned
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " + "TABLE_NAME='plotmePlots' AND column_name='auctionned'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD auctionned boolean NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();

                // Auctionenddate
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " + "TABLE_NAME='plotmePlots' AND column_name='auctionenddate'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD auctionenddate varchar(16) NULL;");
                    conn.commit();
                }
                set.close();

                // Currentbidder
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " + "TABLE_NAME='plotmePlots' AND column_name='currentbidder'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD currentbidder varchar(32) NULL;");
                    conn.commit();
                }
                set.close();

                // Currentbid
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " + "TABLE_NAME='plotmePlots' AND column_name='currentbid'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD currentbid double NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();

                /*** END Version 0.8 changes ***/

                /*** START Version 0.13d changes ***/

                // OwnerId
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " + "TABLE_NAME='plotmePlots' AND column_name='ownerid'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD ownerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();

                // Allowed playerid
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " + "TABLE_NAME='plotmeAllowed' AND column_name='playerid'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmeAllowed ADD playerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();

                // Denied playerid
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " + "TABLE_NAME='plotmeDenied' AND column_name='playerid'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmeDenied ADD playerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();

                // Commenter playerid
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " + "TABLE_NAME='plotmeComments' AND column_name='playerid'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmeComments ADD playerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();

                // CurrentBidderId
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND " + "TABLE_NAME='plotmePlots' AND column_name='currentbidderId'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD currentbidderId blob(16) NULL;");
                    conn.commit();
                }
                set.close();

                /*** END Version 0.13d changes ***/
            } else {
                String column;
                boolean found = false;

                /*** START Version 0.8 changes ***/
                // CustomPrice
                set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");
                while (set.next() && !found) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("customprice")) {
                        found = true;
                    }
                }

                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD customprice double NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();
                found = false;

                // ForSale
                set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");

                while (set.next() && !found) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("forsale")) {
                        found = true;
                    }
                }

                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD forsale boolean NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();
                found = false;

                // FinishedDate
                set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");

                while (set.next() && !found) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("finisheddate")) {
                        found = true;
                    }
                }

                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD finisheddate varchar(16) NULL;");
                    conn.commit();
                }
                set.close();
                found = false;

                // Protected
                set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");

                while (set.next() && !found) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("protected")) {
                        found = true;
                    }
                }

                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD protected boolean NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();
                found = false;

                // Auctionned
                set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");

                while (set.next() && !found) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("auctionned")) {
                        found = true;
                    }
                }

                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD auctionned boolean NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();
                found = false;

                // Auctionenddate
                set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");

                while (set.next() && !found) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("auctionenddate")) {
                        found = true;
                    }
                }

                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD auctionenddate varchar(16) NULL;");
                    conn.commit();
                }
                set.close();
                found = false;

                // Currentbidder
                set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");

                while (set.next() && !found) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("currentbidder")) {
                        found = true;
                    }
                }

                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD currentbidder varchar(32) NULL;");
                    conn.commit();
                }
                set.close();
                found = false;

                // Currentbid
                set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");

                while (set.next() && !found) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("currentbid")) {
                        found = true;
                    }
                }

                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD currentbid double NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();
                found = false;
                /*** END Version 0.8 changes ***/

                /*** START Version 0.13d changes ***/

                // OwnerId
                set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");

                while (set.next() && !found) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("ownerid")) {
                        found = true;
                    }
                }

                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD ownerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();
                found = false;

                // Allowed id
                set = statement.executeQuery("PRAGMA table_info(`plotmeAllowed`)");

                while (set.next() && !found) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("playerid")) {
                        found = true;
                    }
                }

                if (!found) {
                    statement.execute("ALTER TABLE plotmeAllowed ADD playerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();
                found = false;

                // Denied id
                set = statement.executeQuery("PRAGMA table_info(`plotmeDenied`)");

                while (set.next() && !found) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("playerid")) {
                        found = true;
                    }
                }

                if (!found) {
                    statement.execute("ALTER TABLE plotmeDenied ADD playerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();
                found = false;

                // Commenter id
                set = statement.executeQuery("PRAGMA table_info(`plotmeComments`)");

                while (set.next() && !found) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("playerid")) {
                        found = true;
                    }
                }

                if (!found) {
                    statement.execute("ALTER TABLE plotmeComments ADD playerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();
                found = false;

                // CurrentBidderId
                set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)");

                while (set.next() && !found) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("currentbidderId")) {
                        found = true;
                    }
                }

                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD currentbidderId blob(16) NULL;");
                    conn.commit();
                }
                set.close();
                found = false;

                /*** END Version 0.13d changes ***/

            }
        } catch (final SQLException ex) {
            AthionPlots.logger.severe("Update table exception :");
            AthionPlots.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Update table exception (on close) :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
    }

    public static Connection initialize() {
        try {
            if (AthionPlots.usemySQL) {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(AthionPlots.mySQLconn, AthionPlots.mySQLuname, AthionPlots.mySQLpass);
                conn.setAutoCommit(false);
            } else {
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection("jdbc:sqlite:" + AthionPlots.configpath + "/plots.db");
                conn.setAutoCommit(false);
            }
        } catch (final SQLException ex) {
            AthionPlots.logger.severe("SQL exception on initialize :");
            AthionPlots.logger.severe("  " + ex.getMessage());
        } catch (final ClassNotFoundException ex) {
            AthionPlots.logger.severe("You need the SQLite/MySQL library. :");
            AthionPlots.logger.severe("  " + ex.getMessage());
        }

        createTable();

        return conn;
    }

    public static Connection getConnection() {
        if (conn == null) {
            conn = initialize();
        }
        if (AthionPlots.usemySQL) {
            try {
                if (!conn.isValid(10)) {
                    conn = initialize();
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Failed to check SQL status :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
        return conn;
    }

    public static void closeConnection() {
        if (conn != null) {
            try {
                if (AthionPlots.usemySQL) {
                    if (conn.isValid(10)) {
                        conn.close();
                    }
                    conn = null;
                } else {
                    conn.close();
                    conn = null;
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Error on Connection close :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
    }

    public static void addPlotComment(final String[] comment, final int commentid, final int idX, final int idZ, final String world) {
        UUID uuid = null;
        if (comment.length > 2) {
            try {
                uuid = UUID.fromString(comment[2]);
            } catch (final IllegalArgumentException e) {}
        }
        addPlotComment(comment, commentid, idX, idZ, world, uuid);
    }

    public static void addPlotComment(final String[] comment, final int commentid, final int idX, final int idZ, final String world, final UUID uuid) {
        PreparedStatement ps = null;
        Connection conn;

        // Comments
        try {
            conn = getConnection();

            ps = conn.prepareStatement("INSERT INTO plotmeComments (idX, idZ, commentid, player, comment, world, playerid) " + "VALUES (?,?,?,?,?,?,?)");

            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setInt(3, commentid);
            ps.setString(4, comment[0]);
            ps.setString(5, comment[1]);
            ps.setString(6, world.toLowerCase());
            if (uuid != null) {
                ps.setBytes(7, UUIDFetcher.toBytes(uuid));
            } else {
                ps.setBytes(7, null);
            }

            ps.executeUpdate();
            conn.commit();

        } catch (final SQLException ex) {
            AthionPlots.logger.severe("Insert Exception :");
            AthionPlots.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Insert Exception (on close) :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
    }

    public static void addPlotDenied(final String player, final int idX, final int idZ, final String world) {
        final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op == null) {
            addPlotDenied(player, null, idX, idZ, world);
        } else {
            addPlotDenied(player, op.getUniqueId(), idX, idZ, world);
        }
    }

    public static void addPlotDenied(final String player, final UUID playerid, final int idX, final int idZ, final String world) {
        PreparedStatement ps = null;
        Connection conn;

        // Denied
        try {
            conn = getConnection();

            ps = conn.prepareStatement("INSERT INTO plotmeDenied (idX, idZ, player, world, playerid) " + "VALUES (?,?,?,?,?)");

            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, player);
            ps.setString(4, world.toLowerCase());
            if (playerid != null) {
                ps.setBytes(5, UUIDFetcher.toBytes(playerid));
            } else {
                ps.setBytes(5, null);
            }

            ps.executeUpdate();
            conn.commit();

            if (playerid == null) {
                fetchDeniedUUIDAsync(idX, idZ, world, player);
            }

        } catch (final SQLException ex) {
            AthionPlots.logger.severe("Insert Exception :");
            AthionPlots.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Insert Exception (on close) :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
    }

    public static void deletePlot(final int idX, final int idZ, final String world) {
        PreparedStatement ps = null;
        final ResultSet set = null;
        try {
            final Connection conn = getConnection();

            ps = conn.prepareStatement("DELETE FROM plotmeComments WHERE idX = ? and idZ = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, world);
            ps.executeUpdate();
            conn.commit();
            ps.close();

            ps = conn.prepareStatement("DELETE FROM plotmeAllowed WHERE idX = ? and idZ = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, world);
            ps.executeUpdate();
            conn.commit();
            ps.close();

            ps = conn.prepareStatement("DELETE FROM plotmeDenied WHERE idX = ? and idZ = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, world);
            ps.executeUpdate();
            conn.commit();
            ps.close();

            ps = conn.prepareStatement("DELETE FROM plotmePlots WHERE idX = ? and idZ = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, world);
            ps.executeUpdate();
            conn.commit();
            ps.close();

        } catch (final SQLException ex) {
            AthionPlots.logger.severe("Delete Exception :");
            AthionPlots.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Delete Exception (on close) :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
    }

    public static void deletePlotComment(final int idX, final int idZ, final int commentid, final String world) {
        PreparedStatement ps = null;
        final ResultSet set = null;
        try {
            final Connection conn = getConnection();

            ps = conn.prepareStatement("DELETE FROM plotmeComments WHERE idX = ? and idZ = ? and commentid = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setInt(3, commentid);
            ps.setString(4, world);
            ps.executeUpdate();
            conn.commit();

        } catch (final SQLException ex) {
            AthionPlots.logger.severe("Delete Exception :");
            AthionPlots.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Delete Exception (on close) :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
    }

    @Deprecated
    public static void deletePlotAllowed(final int idX, final int idZ, final String player, final String world) {
        final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op == null) {
            deletePlotAllowed(idX, idZ, player, null, world);
        } else {
            deletePlotAllowed(idX, idZ, player, op.getUniqueId(), world);
        }
    }

    public static void deletePlotAllowed(final int idX, final int idZ, final String player, final UUID playerid, final String world) {
        PreparedStatement ps = null;
        final ResultSet set = null;

        try {
            final Connection conn = getConnection();

            if (playerid == null) {
                ps = conn.prepareStatement("DELETE FROM plotmeAllowed WHERE idX = ? and idZ = ? and player = ? and LOWER(world) = ?");
                ps.setString(3, player);
            } else {
                ps = conn.prepareStatement("DELETE FROM plotmeAllowed WHERE idX = ? and idZ = ? and playerid = ? and LOWER(world) = ?");
                ps.setBytes(3, UUIDFetcher.toBytes(playerid));
            }
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(4, world);
            ps.executeUpdate();
            conn.commit();

        } catch (final SQLException ex) {
            AthionPlots.logger.severe("Delete Exception :");
            AthionPlots.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Delete Exception (on close) :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
    }

    @Deprecated
    public static void deletePlotDenied(final int idX, final int idZ, final String player, final String world) {
        final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op == null) {
            deletePlotDenied(idX, idZ, player, null, world);
        } else {
            deletePlotDenied(idX, idZ, player, op.getUniqueId(), world);
        }
    }

    public static void deletePlotDenied(final int idX, final int idZ, final String player, final UUID playerid, final String world) {
        PreparedStatement ps = null;
        final ResultSet set = null;

        try {
            final Connection conn = getConnection();

            if (playerid == null) {
                ps = conn.prepareStatement("DELETE FROM plotmeDenied WHERE idX = ? and idZ = ? and player = ? and LOWER(world) = ?");
                ps.setString(3, player);
            } else {
                ps = conn.prepareStatement("DELETE FROM plotmeDenied WHERE idX = ? and idZ = ? and playerid = ? and LOWER(world) = ?");
                ps.setBytes(3, UUIDFetcher.toBytes(playerid));
            }
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(4, world);
            ps.executeUpdate();
            conn.commit();

        } catch (final SQLException ex) {
            AthionPlots.logger.severe("Delete Exception :");
            AthionPlots.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Delete Exception (on close) :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
    }

    public static HashMap<String, AthionPlot> getPlots(final String world) {
        final HashMap<String, AthionPlot> ret = new HashMap<String, AthionPlot>();
        Statement statementPlot = null;
        Statement statementAllowed = null;
        Statement statementDenied = null;
        Statement statementComment = null;
        ResultSet setPlots = null;
        ResultSet setAllowed = null;
        ResultSet setDenied = null;
        ResultSet setComments = null;

        try {

            final Connection conn = getConnection();

            statementPlot = conn.createStatement();
            setPlots = statementPlot.executeQuery("SELECT * FROM plotmePlots WHERE LOWER(world) = '" + world + "'");
            int size = 0;
            while (setPlots.next()) {
                size++;
                final int idX = setPlots.getInt("idX");
                final int idZ = setPlots.getInt("idZ");
                final String owner = setPlots.getString("owner");
                final int topX = setPlots.getInt("topX");
                final int bottomX = setPlots.getInt("bottomX");
                final int topZ = setPlots.getInt("topZ");
                final int bottomZ = setPlots.getInt("bottomZ");
                final String biome = setPlots.getString("biome");
                final java.sql.Date expireddate = setPlots.getDate("expireddate");
                final boolean finished = setPlots.getBoolean("finished");
                final AthionPlayers allowed = new AthionPlayers();
                final AthionPlayers denied = new AthionPlayers();
                final List<String[]> comments = new ArrayList<String[]>();
                final double customprice = setPlots.getDouble("customprice");
                final boolean forsale = setPlots.getBoolean("forsale");
                final String finisheddate = setPlots.getString("finisheddate");
                final boolean protect = setPlots.getBoolean("protected");
                final String currentbidder = setPlots.getString("currentbidder");
                final double currentbid = setPlots.getDouble("currentbid");
                final boolean auctionned = setPlots.getBoolean("auctionned");

                final byte[] byOwner = setPlots.getBytes("ownerId");
                final byte[] byBidder = setPlots.getBytes("currentbidderid");

                UUID ownerId = null;
                UUID currentbidderid = null;

                if (byOwner != null) {
                    ownerId = UUIDFetcher.fromBytes(byOwner);
                }
                if (byBidder != null) {
                    currentbidderid = UUIDFetcher.fromBytes(byBidder);
                }

                statementAllowed = conn.createStatement();
                setAllowed = statementAllowed.executeQuery("SELECT * FROM plotmeAllowed WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND LOWER(world) = '" + world + "'");

                while (setAllowed.next()) {
                    final byte[] byPlayerId = setAllowed.getBytes("playerid");
                    if (byPlayerId == null) {
                        allowed.put(setAllowed.getString("player"));
                    } else {
                        allowed.put(setAllowed.getString("player"), UUIDFetcher.fromBytes(byPlayerId));
                    }
                }

                if (setAllowed != null) {
                    setAllowed.close();
                }

                statementDenied = conn.createStatement();
                setDenied = statementDenied.executeQuery("SELECT * FROM plotmeDenied WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND LOWER(world) = '" + world + "'");

                while (setDenied.next()) {
                    final byte[] byPlayerId = setDenied.getBytes("playerid");
                    if (byPlayerId == null) {
                        denied.put(setDenied.getString("player"));
                    } else {
                        denied.put(setDenied.getString("player"), UUIDFetcher.fromBytes(byPlayerId));
                    }
                }

                if (setDenied != null) {
                    setDenied.close();
                }

                statementComment = conn.createStatement();
                setComments = statementComment.executeQuery("SELECT * FROM plotmeComments WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND LOWER(world) = '" + world + "'");

                while (setComments.next()) {
                    final String[] comment = new String[3];
                    comment[0] = setComments.getString("player");
                    comment[1] = setComments.getString("comment");

                    final byte[] byPlayerId = setComments.getBytes("playerid");
                    if (byPlayerId != null) {
                        comment[2] = UUIDFetcher.fromBytes(byPlayerId).toString();
                    }

                    comments.add(comment);
                }

                final AthionPlot plot = new AthionPlot(owner, ownerId, world, topX, bottomX, topZ, bottomZ, biome, expireddate, finished, allowed, comments, "" + idX + ";" + idZ, customprice,
                forsale, finisheddate, protect, currentbidder, currentbidderid, currentbid, auctionned, denied);
                ret.put("" + idX + ";" + idZ, plot);
            }
            AthionPlots.logger.info(" " + size + " plots loaded");
        } catch (final SQLException ex) {
            AthionPlots.logger.severe("Load Exception :");
            AthionPlots.logger.severe("  " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (statementPlot != null) {
                    statementPlot.close();
                }
                if (statementAllowed != null) {
                    statementAllowed.close();
                }
                if (statementComment != null) {
                    statementComment.close();
                }
                if (setPlots != null) {
                    setPlots.close();
                }
                if (setComments != null) {
                    setComments.close();
                }
                if (setAllowed != null) {
                    setAllowed.close();
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Load Exception (on close) :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
        return ret;
    }

    public static void plotConvertToUUIDAsynchronously() {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(AthionPlots.self, new Runnable() {
            @Override
            public void run() {
                AthionPlots.logger.info("Checking if conversion to UUID needed...");

                boolean boConversion = false;
                Statement statementPlayers = null;
                PreparedStatement psOwnerId = null;
                PreparedStatement psCurrentBidderId = null;
                PreparedStatement psAllowedPlayerId = null;
                PreparedStatement psDeniedPlayerId = null;
                PreparedStatement psCommentsPlayerId = null;

                PreparedStatement psDeleteOwner = null;
                PreparedStatement psDeleteCurrentBidder = null;
                PreparedStatement psDeleteAllowed = null;
                PreparedStatement psDeleteDenied = null;
                PreparedStatement psDeleteComments = null;

                ResultSet setPlayers = null;
                int nbConverted = 0;
                String sql = "";
                int count = 0;

                try {
                    final Connection conn = getConnection();

                    // Get all the players
                    statementPlayers = conn.createStatement();
                    // Exclude groups and names with * or missing
                    sql = "SELECT LOWER(owner) as Name FROM plotmePlots WHERE NOT owner IS NULL AND Not owner LIKE 'group:%' AND Not owner LIKE '%*%' AND ownerid IS NULL GROUP BY LOWER(owner) ";
                    sql = sql + "UNION SELECT LOWER(currentbidder) as Name FROM plotmePlots WHERE NOT currentbidder IS NULL AND currentbidderid IS NULL GROUP BY LOWER(currentbidder) ";
                    sql = sql
                    + "UNION SELECT LOWER(player) as Name FROM plotmeAllowed WHERE NOT player IS NULL AND Not player LIKE 'group:%' AND Not player LIKE '%*%' AND playerid IS NULL GROUP BY LOWER(player) ";
                    sql = sql
                    + "UNION SELECT LOWER(player) as Name FROM plotmeDenied WHERE NOT player IS NULL AND Not player LIKE 'group:%' AND Not player LIKE '%*%' AND playerid IS NULL GROUP BY LOWER(player) ";
                    sql = sql
                    + "UNION SELECT LOWER(player) as Name FROM plotmeComments WHERE NOT player IS NULL AND Not player LIKE 'group:%' AND Not player LIKE '%*%' AND playerid IS NULL GROUP BY LOWER(player)";

                    AthionPlots.logger.info("Verifying if database needs conversion");

                    setPlayers = statementPlayers.executeQuery(sql);

                    if (setPlayers.next()) {

                        final List<String> names = new ArrayList<String>();

                        //Prepare delete statements
                        psDeleteOwner = conn.prepareStatement("UPDATE plotmePlots SET owner = '' WHERE owner = ? ");
                        psDeleteCurrentBidder = conn.prepareStatement("UPDATE plotmePlots SET currentbidder = null WHERE currentbidder = ? ");
                        psDeleteAllowed = conn.prepareStatement("DELETE FROM plotmeAllowed WHERE player = ? ");
                        psDeleteDenied = conn.prepareStatement("DELETE FROM plotmeDenied WHERE player = ? ");
                        psDeleteComments = conn.prepareStatement("DELETE FROM plotmeComments WHERE player = ? ");

                        AthionPlots.logger.info("Starting to convert plots to UUID");
                        do {
                            final String name = setPlayers.getString("Name");
                            if (!name.equals("")) {
                                if (name.matches("^[a-zA-Z0-9_]{1,16}$")) {
                                    names.add(name);
                                } else {
                                    AthionPlots.logger.warning("Invalid name found : " + name + ". Removing from database.");
                                    psDeleteOwner.setString(1, name);
                                    psDeleteOwner.executeUpdate();
                                    psDeleteCurrentBidder.setString(1, name);
                                    psDeleteCurrentBidder.executeUpdate();
                                    psDeleteAllowed.setString(1, name);
                                    psDeleteAllowed.executeUpdate();
                                    psDeleteDenied.setString(1, name);
                                    psDeleteDenied.executeUpdate();
                                    psDeleteComments.setString(1, name);
                                    psDeleteComments.executeUpdate();
                                    conn.commit();
                                }
                            }
                        } while (setPlayers.next());

                        psDeleteOwner.close();
                        psDeleteCurrentBidder.close();
                        psDeleteAllowed.close();
                        psDeleteDenied.close();
                        psDeleteComments.close();

                        final UUIDFetcher fetcher = new UUIDFetcher(names);

                        Map<String, UUID> response = null;

                        try {
                            AthionPlots.logger.info("Fetching " + names.size() + " UUIDs from Mojang servers...");
                            response = fetcher.call();
                            AthionPlots.logger.info("Finished fetching " + response.size() + " UUIDs. Starting database update.");
                        } catch (final Exception e) {
                            AthionPlots.logger.warning("Exception while running UUIDFetcher");
                            e.printStackTrace();
                        }

                        if (response.size() > 0) {
                            psOwnerId = conn.prepareStatement("UPDATE plotmePlots SET ownerid = ? WHERE LOWER(owner) = ? AND ownerid IS NULL");
                            psCurrentBidderId = conn.prepareStatement("UPDATE plotmePlots SET currentbidderid = ? WHERE LOWER(currentbidder) = ? AND currentbidderid IS NULL");
                            psAllowedPlayerId = conn.prepareStatement("UPDATE plotmeAllowed SET playerid = ? WHERE LOWER(player) = ? AND playerid IS NULL");
                            psDeniedPlayerId = conn.prepareStatement("UPDATE plotmeDenied SET playerid = ? WHERE LOWER(player) = ? AND playerid IS NULL");
                            psCommentsPlayerId = conn.prepareStatement("UPDATE plotmeComments SET playerid = ? WHERE LOWER(player) = ? AND playerid IS NULL");

                            for (final String key : response.keySet()) {
                                count = 0;
                                // Owner
                                psOwnerId.setBytes(1, UUIDFetcher.toBytes(response.get(key)));
                                psOwnerId.setString(2, key.toLowerCase());
                                count += psOwnerId.executeUpdate();
                                // Bidder
                                psCurrentBidderId.setBytes(1, UUIDFetcher.toBytes(response.get(key)));
                                psCurrentBidderId.setString(2, key.toLowerCase());
                                count += psCurrentBidderId.executeUpdate();
                                // Allowed
                                psAllowedPlayerId.setBytes(1, UUIDFetcher.toBytes(response.get(key)));
                                psAllowedPlayerId.setString(2, key.toLowerCase());
                                count += psAllowedPlayerId.executeUpdate();
                                // Denied
                                psDeniedPlayerId.setBytes(1, UUIDFetcher.toBytes(response.get(key)));
                                psDeniedPlayerId.setString(2, key.toLowerCase());
                                count += psDeniedPlayerId.executeUpdate();
                                // Commenter
                                psCommentsPlayerId.setBytes(1, UUIDFetcher.toBytes(response.get(key)));
                                psCommentsPlayerId.setString(2, key.toLowerCase());
                                psCommentsPlayerId.executeUpdate();
                                conn.commit();
                                if (count > 0) {
                                    nbConverted++;
                                } else {
                                    AthionPlots.logger.warning("Unable to update player '" + key + "'");
                                }
                            }

                            psOwnerId.close();
                            psCurrentBidderId.close();
                            psAllowedPlayerId.close();
                            psDeniedPlayerId.close();
                            psCommentsPlayerId.close();

                            //Update plot information
                            for (final AthionMaps pmi : AthionPlots.AthionMaps.values()) {
                                for (final AthionPlot plot : pmi.plots.values()) {
                                    for (final Entry<String, UUID> player : response.entrySet()) {
                                        //Owner
                                        if ((plot.ownerId == null) && (plot.owner != null) && plot.owner.equalsIgnoreCase(player.getKey())) {
                                            plot.owner = player.getKey();
                                            plot.ownerId = player.getValue();
                                        }

                                        //Bidder
                                        if ((plot.currentbidderId == null) && (plot.currentbidder != null) && plot.currentbidder.equalsIgnoreCase(player.getKey())) {
                                            plot.currentbidder = player.getKey();
                                            plot.currentbidderId = player.getValue();
                                        }

                                        //Allowed
                                        plot.allowed.replace(player.getKey(), player.getValue());

                                        //Denied
                                        plot.denied.replace(player.getKey(), player.getValue());

                                        //Comments
                                        for (final String[] comment : plot.comments) {
                                            if ((comment.length > 2) && (comment[2] == null) && (comment[0] != null) && comment[0].equalsIgnoreCase(player.getKey())) {
                                                comment[0] = player.getKey();
                                                comment[2] = player.getValue().toString();
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        boConversion = true;
                        AthionPlots.logger.info(nbConverted + " players converted");
                    }
                    setPlayers.close();
                    statementPlayers.close();

                    if (boConversion) {
                        AthionPlots.logger.info("Plot conversion finished");
                    } else {
                        AthionPlots.logger.info("No plot conversion needed");
                    }
                } catch (final SQLException ex) {
                    AthionPlots.logger.severe("Conversion to UUID failed :");
                    AthionPlots.logger.severe("  " + ex.getMessage());
                    for (final StackTraceElement e : ex.getStackTrace()) {
                        AthionPlots.logger.severe("  " + e.toString());
                    }
                } finally {
                    try {
                        if (statementPlayers != null) {
                            statementPlayers.close();
                        }
                        if (psOwnerId != null) {
                            psOwnerId.close();
                        }
                        if (psCurrentBidderId != null) {
                            psCurrentBidderId.close();
                        }
                        if (psAllowedPlayerId != null) {
                            psAllowedPlayerId.close();
                        }
                        if (psDeniedPlayerId != null) {
                            psDeniedPlayerId.close();
                        }
                        if (psCommentsPlayerId != null) {
                            psCommentsPlayerId.close();
                        }
                        if (setPlayers != null) {
                            setPlayers.close();
                        }
                        if (psDeleteOwner != null) {
                            psDeleteOwner.close();
                        }
                        if (psDeleteCurrentBidder != null) {
                            psDeleteCurrentBidder.close();
                        }
                        if (psDeleteAllowed != null) {
                            psDeleteAllowed.close();
                        }
                        if (psDeleteDenied != null) {
                            psDeleteDenied.close();
                        }
                        if (psDeleteComments != null) {
                            psDeleteComments.close();
                        }
                    } catch (final SQLException ex) {
                        AthionPlots.logger.severe("Conversion to UUID failed (on close) :");
                        AthionPlots.logger.severe("  " + ex.getMessage());
                        for (final StackTraceElement e : ex.getStackTrace()) {
                            AthionPlots.logger.severe("  " + e.toString());
                        }
                    }
                }
            }
        });
    }

    public static void addPlot(final AthionPlot plot, final int idX, final int idZ, final World w) {
        addPlot(plot, idX, idZ, AthionCore.topX(plot.id, w), AthionCore.bottomX(plot.id, w), AthionCore.topZ(plot.id, w), AthionCore.bottomZ(plot.id, w));
    }

    public static void addPlot(final AthionPlot plot, final int idX, final int idZ, final int topX, final int bottomX, final int topZ, final int bottomZ) {
        PreparedStatement ps = null;
        Connection conn;
        final StringBuilder strSql = new StringBuilder();

        // Plots
        try {
            conn = getConnection();

            strSql.append("INSERT INTO plotmePlots (idX, idZ, owner, world, topX, bottomX, topZ, bottomZ, ");
            strSql.append("biome, expireddate, finished, customprice, forsale, finisheddate, protected,");
            strSql.append("auctionned, auctionenddate, currentbid, currentbidder, currentbidderId, ownerId) ");
            strSql.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            ps = conn.prepareStatement(strSql.toString());
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, plot.owner);
            ps.setString(4, plot.world.toLowerCase());
            ps.setInt(5, topX);
            ps.setInt(6, bottomX);
            ps.setInt(7, topZ);
            ps.setInt(8, bottomZ);
            ps.setString(9, plot.biome.name());
            ps.setDate(10, plot.expireddate);
            ps.setBoolean(11, plot.finished);
            ps.setDouble(12, plot.customprice);
            ps.setBoolean(13, plot.forsale);
            ps.setString(14, plot.finisheddate);
            ps.setBoolean(15, plot.protect);
            ps.setBoolean(16, plot.auctionned);
            ps.setDate(17, null); //not implemented
            ps.setDouble(18, plot.currentbid);
            ps.setString(19, plot.currentbidder);
            if (plot.currentbidderId != null) {
                ps.setBytes(20, UUIDFetcher.toBytes(plot.currentbidderId));
            } else {
                ps.setBytes(20, null);
            }
            if (plot.ownerId != null) {
                ps.setBytes(21, UUIDFetcher.toBytes(plot.ownerId));
            } else {
                ps.setBytes(21, null);
            }

            ps.executeUpdate();
            conn.commit();

            if ((plot.allowed != null) && (plot.allowed.getAllPlayers() != null)) {
                final HashMap<String, UUID> allowed = plot.allowed.getAllPlayers();
                for (final String key : allowed.keySet()) {
                    addPlotAllowed(key, allowed.get(key), idX, idZ, plot.world);
                }
            }

            if ((plot.denied != null) && (plot.denied.getAllPlayers() != null)) {
                final HashMap<String, UUID> denied = plot.denied.getAllPlayers();
                for (final String key : denied.keySet()) {
                    //addPlotDenied(key, denied.get(key), idX, idZ, plot.world);
                }
            }

            if ((plot.comments != null) && (plot.comments.size() > 0)) {
                for (final String[] comments : plot.comments) {
                    String strUUID = "";
                    if (comments.length >= 3) {
                        strUUID = comments[2];
                        try {
                            UUID.fromString(strUUID);
                        } catch (final Exception e) {}
                    }
                }
            }

            if ((plot.owner != null) && !plot.owner.equals("") && (plot.ownerId == null)) {
                fetchOwnerUUIDAsync(idX, idZ, plot.world.toLowerCase(), plot.owner);
            }

            if ((plot.currentbidder != null) && !plot.currentbidder.equals("") && (plot.currentbidderId == null)) {
                fetchBidderUUIDAsync(idX, idZ, plot.world.toLowerCase(), plot.currentbidder);
            }

        } catch (final SQLException ex) {
            AthionPlots.logger.severe("Insert Exception :");
            AthionPlots.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Insert Exception (on close) :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
    }

    public static void addPlotAllowed(final String player, final int idX, final int idZ, final String world) {
        addPlotAllowed(player, null, idX, idZ, world);
    }

    public static void addPlotAllowed(final String player, final UUID playerid, final int idX, final int idZ, final String world) {
        PreparedStatement ps = null;
        Connection conn;

        // Allowed
        try {
            conn = getConnection();

            ps = conn.prepareStatement("INSERT INTO plotmeAllowed (idX, idZ, player, world, playerid) " + "VALUES (?,?,?,?,?)");

            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, player);
            ps.setString(4, world.toLowerCase());
            if (playerid != null) {
                ps.setBytes(5, UUIDFetcher.toBytes(playerid));
            } else {
                ps.setBytes(5, null);
            }

            ps.executeUpdate();
            conn.commit();

            if (playerid == null) {
                fetchAllowedUUIDAsync(idX, idZ, world, player);
            }

        } catch (final SQLException ex) {
            AthionPlots.logger.severe("Insert Exception :");
            AthionPlots.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Insert Exception (on close) :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
    }

    public static void fetchOwnerUUIDAsync(final int idX, final int idZ, final String world, final String owner) {
        _fetchUUIDAsync(idX, idZ, world, "owner", owner);
    }

    public static void fetchBidderUUIDAsync(final int idX, final int idZ, final String world, final String bidder) {
        _fetchUUIDAsync(idX, idZ, world, "bidder", bidder);
    }

    public static void fetchAllowedUUIDAsync(final int idX, final int idZ, final String world, final String allowed) {
        _fetchUUIDAsync(idX, idZ, world, "allowed", allowed);
    }

    public static void fetchDeniedUUIDAsync(final int idX, final int idZ, final String world, final String denied) {
        _fetchUUIDAsync(idX, idZ, world, "denied", denied);
    }

    public static void updatePlot(final int idX, final int idZ, final String world, final String field, final Object value) {
        PreparedStatement ps = null;
        Connection conn;

        // Plots
        try {
            conn = getConnection();

            ps = conn.prepareStatement("UPDATE plotmePlots SET " + field + " = ? " + "WHERE idX = ? AND idZ = ? AND world = ?");

            if (value instanceof UUID) {
                ps.setBytes(1, UUIDFetcher.toBytes((UUID) value));
            } else {
                ps.setObject(1, value);
            }
            ps.setInt(2, idX);
            ps.setInt(3, idZ);
            ps.setString(4, world.toLowerCase());

            ps.executeUpdate();
            conn.commit();

            if (field.equalsIgnoreCase("owner")) {
                fetchOwnerUUIDAsync(idX, idZ, world, value.toString());
            } else if (field.equalsIgnoreCase("currentbidder")) {
                fetchBidderUUIDAsync(idX, idZ, world, value.toString());
            }

        } catch (final SQLException ex) {
            AthionPlots.logger.severe("Insert Exception :");
            AthionPlots.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Insert Exception (on close) :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
    }

    public static void updateTable(final String tablename, final int idX, final int idZ, final String world, final String field, final Object value) {
        PreparedStatement ps = null;
        Connection conn;

        // Plots
        try {
            conn = getConnection();

            ps = conn.prepareStatement("UPDATE " + tablename + " SET " + field + " = ? " + "WHERE idX = ? AND idZ = ? AND world = ?");

            if (value instanceof UUID) {
                ps.setBytes(1, UUIDFetcher.toBytes((UUID) value));
            } else {
                ps.setObject(1, value);
            }
            ps.setInt(2, idX);
            ps.setInt(3, idZ);
            ps.setString(4, world.toLowerCase());

            ps.executeUpdate();
            conn.commit();

            if (field.equalsIgnoreCase("owner")) {
                fetchOwnerUUIDAsync(idX, idZ, world, value.toString());
            } else if (field.equalsIgnoreCase("currentbidder")) {
                fetchBidderUUIDAsync(idX, idZ, world, value.toString());
            } else if (field.equalsIgnoreCase("player")) {
                if (tablename.equalsIgnoreCase("plotmeAllowed")) {

                }
            }

        } catch (final SQLException ex) {
            AthionPlots.logger.severe("Insert Exception :");
            AthionPlots.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (final SQLException ex) {
                AthionPlots.logger.severe("Insert Exception (on close) :");
                AthionPlots.logger.severe("  " + ex.getMessage());
            }
        }
    }

    private static void _fetchUUIDAsync(final int idX, final int idZ, final String world, final String Property, final String name) {
        if (AthionPlots.self.initialized) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(AthionPlots.self, new Runnable() {
                @Override
                public void run() {

                    PreparedStatement ps = null;

                    try {
                        final Connection conn = getConnection();

                        @SuppressWarnings("deprecation")
                        final Player p = Bukkit.getPlayerExact(name);
                        UUID uuid = null;
                        String newname = name;

                        if (p != null) {
                            uuid = p.getUniqueId();
                            newname = p.getName();
                        } else {
                            final List<String> names = new ArrayList<String>();

                            names.add(name);

                            final UUIDFetcher fetcher = new UUIDFetcher(names);

                            Map<String, UUID> response = null;

                            try {
                                AthionPlots.logger.info("Fetching " + names.size() + " UUIDs from Mojang servers...");
                                response = fetcher.call();
                                AthionPlots.logger.info("Received " + response.size() + " UUIDs. Starting database update...");

                                if (response.size() > 0) {
                                    uuid = response.values().toArray(new UUID[0])[0];
                                    newname = response.keySet().toArray(new String[0])[0];
                                }
                            } catch (final IOException e) {
                                AthionPlots.logger.warning("Unable to connect to Mojang server!");
                            } catch (final Exception e) {
                                AthionPlots.logger.warning("Exception while running UUIDFetcher");
                                e.printStackTrace();
                            }
                        }

                        switch (Property) {
                            case "owner":
                                ps = conn.prepareStatement("UPDATE plotmePlots SET ownerid = ?, owner = ? WHERE LOWER(owner) = ? AND idX = '"
                                + idX
                                + "' AND idZ = '"
                                + idZ
                                + "' AND LOWER(world) = '"
                                + world
                                + "'");
                                break;
                            case "bidder":
                                ps = conn.prepareStatement("UPDATE plotmePlots SET currentbidderid = ?, currentbidder = ? WHERE LOWER(currentbidder) = ? AND idX = '"
                                + idX
                                + "' AND idZ = '"
                                + idZ
                                + "' AND LOWER(world) = '"
                                + world
                                + "'");
                                break;
                            case "allowed":
                                ps = conn.prepareStatement("UPDATE plotmeAllowed SET playerid = ?, player = ? WHERE LOWER(player) = ? AND idX = '"
                                + idX
                                + "' AND idZ = '"
                                + idZ
                                + "' AND LOWER(world) = '"
                                + world
                                + "'");
                                break;
                            case "denied":
                                ps = conn.prepareStatement("UPDATE plotmeDenied SET playerid = ?, player = ? WHERE LOWER(player) = ? AND idX = '"
                                + idX
                                + "' AND idZ = '"
                                + idZ
                                + "' AND LOWER(world) = '"
                                + world
                                + "'");
                                break;
                            default:
                                return;
                        }

                        if (uuid != null) {
                            ps.setBytes(1, UUIDFetcher.toBytes(uuid));
                        } else {
                            ps.setBytes(1, null);
                        }
                        ps.setString(2, newname);
                        ps.setString(3, name.toLowerCase());
                        ps.executeUpdate();
                        conn.commit();

                        ps.close();

                        if (uuid != null) {
                            final AthionPlot plot = AthionCore.getPlotById(world, "" + idX + ";" + idZ);

                            if (plot != null) {
                                switch (Property) {
                                    case "owner":
                                        plot.owner = newname;
                                        plot.ownerId = uuid;
                                        break;
                                    case "bidder":
                                        plot.currentbidder = newname;
                                        plot.currentbidderId = uuid;
                                        break;
                                    case "allowed":
                                        plot.allowed.remove(name);
                                        plot.allowed.put(newname, uuid);
                                        break;
                                    case "denied":
                                        plot.denied.remove(name);
                                        plot.denied.put(newname, uuid);
                                        break;
                                    default:
                                        return;
                                }
                            }

                            if (p == null) {
                                AthionPlots.logger.info("UUID updated to Database!");
                            }
                        }
                    } catch (final SQLException ex) {
                        AthionPlots.logger.severe("Conversion to UUID failed :");
                        AthionPlots.logger.severe("  " + ex.getMessage());
                        for (final StackTraceElement e : ex.getStackTrace()) {
                            AthionPlots.logger.severe("  " + e.toString());
                        }
                    } finally {
                        try {
                            if (ps != null) {
                                ps.close();
                            }
                        } catch (final SQLException ex) {
                            AthionPlots.logger.severe("Conversion to UUID failed (on close) :");
                            AthionPlots.logger.severe("  " + ex.getMessage());
                            for (final StackTraceElement e : ex.getStackTrace()) {
                                AthionPlots.logger.severe("  " + e.toString());
                            }
                        }
                    }
                }
            });
        }
    }

    public static void updatePlotsNewUUID(final UUID uuid, final String newname) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(AthionPlots.self, new Runnable() {
            @Override
            public void run() {
                final PreparedStatement[] pss = new PreparedStatement[5];

                try {
                    final Connection conn = getConnection();

                    pss[0] = conn.prepareStatement("UPDATE plotmePlots SET owner = ? WHERE ownerid = ?");
                    pss[1] = conn.prepareStatement("UPDATE plotmePlots SET currentbidder = ? WHERE currentbidderid = ?");
                    pss[2] = conn.prepareStatement("UPDATE plotmeAllowed SET player = ? WHERE playerid = ?");
                    pss[3] = conn.prepareStatement("UPDATE plotmeDenied SET player = ? WHERE playerid = ?");
                    pss[4] = conn.prepareStatement("UPDATE plotmeComments SET player = ? WHERE playerid = ?");

                    for (final PreparedStatement ps : pss) {
                        ps.setString(1, newname);
                        ps.setBytes(2, UUIDFetcher.toBytes(uuid));
                        ps.executeUpdate();
                    }

                    conn.commit();

                    for (final PreparedStatement ps : pss) {
                        ps.close();
                    }

                } catch (final SQLException ex) {
                    AthionPlots.logger.severe("Update player in database from uuid failed :");
                    AthionPlots.logger.severe("  " + ex.getMessage());
                    for (final StackTraceElement e : ex.getStackTrace()) {
                        AthionPlots.logger.severe("  " + e.toString());
                    }
                } finally {
                    try {
                        for (final PreparedStatement ps : pss) {
                            if (ps != null) {
                                ps.close();
                            }
                        }
                    } catch (final SQLException ex) {
                        AthionPlots.logger.severe("Update player in database from uuid failed (on close) :");
                        AthionPlots.logger.severe("  " + ex.getMessage());
                        for (final StackTraceElement e : ex.getStackTrace()) {
                            AthionPlots.logger.severe("  " + e.toString());
                        }
                    }
                }
            }
        });
    }

}
