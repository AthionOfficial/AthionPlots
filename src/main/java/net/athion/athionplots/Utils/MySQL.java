package net.athion.athionplots.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.plugin.Plugin;

public class MySQL extends Database {
    private final String user;
    private final String database;
    private final String password;
    private final String port;
    private final String hostname;

    private Connection connection;

    /**
     * Creates a new MySQL instance
     *
     * @param plugin   Plugin instance
     * @param hostname Name of the host
     * @param port     Port number
     * @param database Database name
     * @param username Username
     * @param password Password
     */
    public MySQL(final Plugin plugin, final String hostname, final String port, final String database, final String username, final String password) {
        super(plugin);
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        user = username;
        this.password = password;
        connection = null;
    }

    @Override
    public Connection openConnection() throws SQLException, ClassNotFoundException {
        if (checkConnection()) {
            return connection;
        }
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database, user, password);
        return connection;
    }

    @Override
    public boolean checkConnection() throws SQLException {
        return (connection != null) && !connection.isClosed();
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public boolean closeConnection() throws SQLException {
        if (connection == null) {
            return false;
        }
        connection.close();
        return true;
    }

    @Override
    public ResultSet querySQL(final String query) throws SQLException, ClassNotFoundException {
        if (checkConnection()) {
            openConnection();
        }

        final Statement statement = connection.createStatement();

        final ResultSet result = statement.executeQuery(query);

        return result;
    }

    @Override
    public int updateSQL(final String query) throws SQLException, ClassNotFoundException {
        if (checkConnection()) {
            openConnection();
        }

        final Statement statement = connection.createStatement();

        final int result = statement.executeUpdate(query);

        return result;
    }

}
