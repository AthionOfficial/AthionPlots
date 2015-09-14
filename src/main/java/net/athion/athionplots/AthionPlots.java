package net.athion.athionplots;

/**
 * AthionPlots
 * Author: Travis506 @ PlotMe by ZachBora
 * Source: Used Code by ZachBora @ PlotMe
 * Description: AthionPlots was intended to be a replica, exactly, of PlotMe, with modifications, changes and other features added. These features were
 * provided by Travis506, for a paid-project or paid-for-services project. This plugin was never sold, nor shall be distributed, and will remain private use.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionGen;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionSQL;
import net.athion.athionplots.Listeners.AthionListenerBlock;
import net.athion.athionplots.Listeners.AthionListenerMain;
import net.athion.athionplots.Listeners.AthionListenerWE;
import net.athion.athionplots.WorldEdit.AthionWorldEdit;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class AthionPlots extends JavaPlugin {

    public static Logger logger = null;
    public static String NAME;
    public static String PREFIX;
    public static Boolean usemySQL;
    public static String mySQLuname;
    public static String mySQLpass;
    public static String mySQLconn;
    public static String configpath;
    public static Boolean globalUseEconomy;
    public static Boolean advancedlogging;
    public static String language;
    public static Boolean allowWorldTeleport;
    public static Boolean autoUpdate;
    public static Boolean allowToBlock;
    public static Boolean allowToMerge;
    public static Boolean allowToSchematic;
    public static AthionPlots self = null;
    public static WorldEditPlugin worldeditplugin = null;
    public static AthionWorldEdit athionworldedit = null;
    public static Economy economy = null;
    public static Boolean usinglwc = false;
    public static ConcurrentHashMap<String, AthionMaps> AthionMaps = null;
    public static World worldcurrentlyprocessingexpired;
    public static CommandSender cscurrentlyprocessingexpired;
    public static Integer counterexpired;
    public static Integer nbperdeletionprocessingexpired;
    public static Boolean defaultWEAnywhere;
    private static HashSet<String> playersignoringwelimit = null;
    private static HashMap<String, String> captions;
    public Boolean initialized = false;
    PluginManager pm;

    public static void initialize() {

        updateWorldYml("bukkit.yml");
        updateWorldYml("plugins/Multiverse-Core/worlds.yml");

        final PluginDescriptionFile pdfFile = self.getDescription();
        NAME = pdfFile.getName();
        PREFIX = ChatColor.DARK_PURPLE + "[" + ChatColor.BOLD + NAME + ChatColor.RESET + ChatColor.DARK_PURPLE + "] " + ChatColor.RESET;
        configpath = self.getDataFolder().getAbsolutePath();
        playersignoringwelimit = new HashSet<>();

        if (!self.getDataFolder().exists()) {
            self.getDataFolder().mkdirs();
        }

        final File configfile = new File(configpath, "config.yml");
        final FileConfiguration config = new YamlConfiguration();

        try {
            config.load(configfile);
        } catch (final FileNotFoundException e) {} catch (final IOException e) {
            logger.severe("Can't read the Configuration File.");
            e.printStackTrace();
        } catch (final InvalidConfigurationException e) {
            logger.severe("Invalid Configuration Format.");
            e.printStackTrace();
        }

        config.set("version", self.getDescription().getVersion());
        usemySQL = config.getBoolean("usemySQL", false);
        mySQLconn = config.getString("mySQLconn", "jdbc:mysql://localhost:3306/minecraft");
        mySQLuname = config.getString("mySQLuname", "root");
        mySQLpass = config.getString("mySQLpass", "password");
        globalUseEconomy = config.getBoolean("globalUseEconomy", false);
        advancedlogging = config.getBoolean("AdvancedLogging", false);
        language = config.getString("Language", "english");
        allowWorldTeleport = config.getBoolean("allowWorldTeleport", true);
        defaultWEAnywhere = config.getBoolean("defaultWEAnywhere", false);
        autoUpdate = config.getBoolean("auto-update", false);
        allowToBlock = config.getBoolean("allowToBlock", true);
        allowToMerge = config.getBoolean("allowToMerge", true);
        allowToSchematic = config.getBoolean("allowToSchematic", true);

        ConfigurationSection worlds;

        if (!config.contains("worlds")) {

            worlds = config.createSection("worlds");
            final ConfigurationSection apworld = worlds.createSection("plotworld");

            // Default AP World Configuration Settings
            // Plot Settings
            apworld.set("AutoLimit", 100);
            apworld.set("PathWidth", 7);
            apworld.set("PlotSize", 32);
            // Generation Settings
            apworld.set("BottomBlockID", "7");
            apworld.set("WallBlockID", "44");
            apworld.set("PlotFloorBLockID", "2");
            apworld.set("PlotFillBlockID", "3");
            apworld.set("RoadMainBlockID", "5");
            apworld.set("RoadStripeBlockID", "5:2");
            // AthionPlots World Settings
            apworld.set("RoadHeight", 64);
            apworld.set("DaysToExpiration", 7);
            apworld.set("ProtectedBlocks", "");
            apworld.set("PreventedItems", "");
            apworld.set("ProtectedWallBlockID", "44:4");
            apworld.set("ForSaleWallBlockID", "44:1");
            apworld.set("AuctionWallBlockID", "44:1");
            apworld.set("AutoLinkPlots", true);
            apworld.set("DisableExplosion", true);
            apworld.set("DisableIgnition", true);

            final ConfigurationSection economysection = apworld.createSection("economy");

            economysection.set("UseEconomy", false);
            economysection.set("CanPutOnSale", false);
            economysection.set("CanSellToBank", false);
            economysection.set("RefundClaimPriceOnReset", false);
            economysection.set("RefundClaimPriceOnSetOwner", false);
            economysection.set("ClaimPrice", 0);
            economysection.set("ClearPrice", 0);
            economysection.set("AddPlayerPrice", 0);
            economysection.set("DenyPlayerPrice", 0);
            economysection.set("RemovePlayerPrice", 0);
            economysection.set("UndenyPlayerPrice", 0);
            economysection.set("PlotHomePrice", 0);
            economysection.set("CanCustomizeSellPrice", false);
            economysection.set("SellToPlayerPrice", 0);
            economysection.set("SellToBankPrice", 0);
            economysection.set("BuyFromBankPrice", 0);
            economysection.set("AddCommentPrice", 0);
            economysection.set("BiomeChangePrice", 0);
            economysection.set("ProtectPrice", 0);
            economysection.set("DisposePrice", 0);

            apworld.set("economy", economysection);

            worlds.set("apworld", apworld);

            config.set("worlds", worlds);

        } else {
            worlds = config.getConfigurationSection("worlds");
        }

        AthionMaps = new ConcurrentHashMap<String, AthionMaps>();

        for (final String worldname : worlds.getKeys(false)) {
            final AthionMaps AthionPlotInfo = new AthionMaps();
            final ConfigurationSection CurrentWorld = worlds.getConfigurationSection(worldname);

            AthionPlotInfo.PlotAutoLimit = CurrentWorld.getInt("PlotAutoLimit", 100);
            AthionPlotInfo.PathWidth = CurrentWorld.getInt("PathWidth", 7);
            AthionPlotInfo.PlotSize = CurrentWorld.getInt("PlotSize", 32);

            AthionPlotInfo.BottomBlockId = self.getBlockId(CurrentWorld, "BottomBlockId", "7:0");
            AthionPlotInfo.BottomBlockValue = self.getBlockValue(CurrentWorld, "BottomBlockId", "7:0");
            AthionPlotInfo.WallBlockId = self.getBlockId(CurrentWorld, "WallBlockId", "44:0");
            AthionPlotInfo.WallBlockValue = self.getBlockValue(CurrentWorld, "WallBlockId", "44:0");
            AthionPlotInfo.PlotFloorBlockId = self.getBlockId(CurrentWorld, "PlotFloorBlockId", "2:0");
            AthionPlotInfo.PlotFloorBlockValue = self.getBlockValue(CurrentWorld, "PlotFloorBlockId", "2:0");
            AthionPlotInfo.PlotFillingBlockId = self.getBlockId(CurrentWorld, "PlotFillingBlockId", "3:0");
            AthionPlotInfo.PlotFillingBlockValue = self.getBlockValue(CurrentWorld, "PlotFillingBlockId", "3:0");
            AthionPlotInfo.RoadMainBlockId = self.getBlockId(CurrentWorld, "RoadMainBlockId", "5:0");
            AthionPlotInfo.RoadMainBlockValue = self.getBlockValue(CurrentWorld, "RoadMainBlockId", "5:0");
            AthionPlotInfo.RoadStripeBlockId = self.getBlockId(CurrentWorld, "RoadStripeBlockId", "5:2");
            AthionPlotInfo.RoadStripeBlockValue = self.getBlockValue(CurrentWorld, "RoadStripeBlockId", "5:2");

            AthionPlotInfo.RoadHeight = CurrentWorld.getInt("RoadHeight", CurrentWorld.getInt("WorldHeight", 64));
            if (AthionPlotInfo.RoadHeight > 250) {
                logger.severe("RoadHeight above 250 is unsafe. This is the height at which your road is located. Setting it to 64.");
                AthionPlotInfo.RoadHeight = 64;
            }
            AthionPlotInfo.DaysToExpiration = CurrentWorld.getInt("DaysToExpiration", 7);

            if (CurrentWorld.contains("ProtectedBlocks")) {
                AthionPlotInfo.ProtectedBlocks = CurrentWorld.getIntegerList("ProtectedBlocks");
            } else {
                //tempPlotInfo.ProtectedBlocks = getDefaultProtectedBlocks();
            }

            if (CurrentWorld.contains("PreventedItems")) {
                AthionPlotInfo.PreventedItems = CurrentWorld.getStringList("PreventedItems");
            } else {
                //tempPlotInfo.PreventedItems = getDefaultPreventedItems();
            }

            AthionPlotInfo.ProtectedWallBlockId = CurrentWorld.getString("ProtectedWallBlockId", "44:4");
            AthionPlotInfo.ForSaleWallBlockId = CurrentWorld.getString("ForSaleWallBlockId", "44:1");
            AthionPlotInfo.AuctionWallBlockId = CurrentWorld.getString("AuctionWallBlockId", "44:1");
            AthionPlotInfo.AutoLinkPlots = CurrentWorld.getBoolean("AutoLinkPlots", true);
            AthionPlotInfo.DisableExplosion = CurrentWorld.getBoolean("DisableExplosion", true);
            AthionPlotInfo.DisableIgnition = CurrentWorld.getBoolean("DisableIgnition", true);

            CurrentWorld.set("PlotAutoLimit", AthionPlotInfo.PlotAutoLimit);
            CurrentWorld.set("PathWidth", AthionPlotInfo.PathWidth);
            CurrentWorld.set("PlotSize", AthionPlotInfo.PlotSize);

            CurrentWorld.set("BottomBlockId", self.getBlockValueId(AthionPlotInfo.BottomBlockId, AthionPlotInfo.BottomBlockValue));
            CurrentWorld.set("WallBlockId", self.getBlockValueId(AthionPlotInfo.WallBlockId, AthionPlotInfo.WallBlockValue));
            CurrentWorld.set("PlotFloorBlockId", self.getBlockValueId(AthionPlotInfo.PlotFloorBlockId, AthionPlotInfo.PlotFloorBlockValue));
            CurrentWorld.set("PlotFillingBlockId", self.getBlockValueId(AthionPlotInfo.PlotFillingBlockId, AthionPlotInfo.PlotFillingBlockValue));
            CurrentWorld.set("RoadMainBlockId", self.getBlockValueId(AthionPlotInfo.RoadMainBlockId, AthionPlotInfo.RoadMainBlockValue));
            CurrentWorld.set("RoadStripeBlockId", self.getBlockValueId(AthionPlotInfo.RoadStripeBlockId, AthionPlotInfo.RoadStripeBlockValue));

            CurrentWorld.set("RoadHeight", AthionPlotInfo.RoadHeight);
            CurrentWorld.set("WorldHeight", null);
            CurrentWorld.set("DaysToExpiration", AthionPlotInfo.DaysToExpiration);
            CurrentWorld.set("ProtectedBlocks", AthionPlotInfo.ProtectedBlocks);
            CurrentWorld.set("PreventedItems", AthionPlotInfo.PreventedItems);
            CurrentWorld.set("ProtectedWallBlockId", AthionPlotInfo.ProtectedWallBlockId);
            CurrentWorld.set("ForSaleWallBlockId", AthionPlotInfo.ForSaleWallBlockId);
            CurrentWorld.set("AuctionWallBlockId", AthionPlotInfo.AuctionWallBlockId);
            CurrentWorld.set("AutoLinkPlots", AthionPlotInfo.AutoLinkPlots);
            CurrentWorld.set("DisableExplosion", AthionPlotInfo.DisableExplosion);
            CurrentWorld.set("DisableIgnition", AthionPlotInfo.DisableIgnition);

            worlds.set(worldname, CurrentWorld);

            AthionPlotInfo.plots = AthionSQL.getPlots(worldname.toLowerCase());

            AthionMaps.put(worldname.toLowerCase(), AthionPlotInfo);

            logger.info("World: " + worldname.toLowerCase() + " addedd!");
            logger.info("Worlds Enabled: " + AthionMaps.toString());

        }

        config.set("usemySQL", usemySQL);
        config.set("mySQLconn", mySQLconn);
        config.set("mySQLuname", mySQLuname);
        config.set("mySQLpass", mySQLpass);
        config.set("globalUseEconomy", globalUseEconomy);
        config.set("AdvancedLogging", advancedlogging);
        config.set("Language", language);
        config.set("allowWorldTeleport", allowWorldTeleport);
        config.set("defaultWEAnywhere", defaultWEAnywhere);
        config.set("auto-update", autoUpdate);
        config.set("allowToBlock", allowToBlock);
        config.set("allowToMerge", allowToMerge);
        config.set("allowToSchematic", allowToSchematic);

        try {
            config.save(configfile);
        } catch (final IOException e) {
            logger.severe("Configuration Could Not Be Written.");
        }

        self.loadCaptions();

    }

    public static void updateWorldYml(final String location) {
        try {
            final Path path = Paths.get(location);
            final File file = new File(location);
            if (!file.exists()) {
                return;
            }
            final Charset charset = StandardCharsets.UTF_8;
            String content = new String(Files.readAllBytes(path), charset);
            content = content.replaceAll("PlotMe-DefaultGenerator", "PlotMe");
            Files.write(path, content.getBytes(charset));
        } catch (final Exception e) {}
    }

    public static int getPlotLimit(final Player p) {
        int max = -2;

        final int maxlimit = 5;

        if (p.hasPermission("plotme.limit.*")) {
            return -1;
        } else {
            for (int ctr = 0; ctr < maxlimit; ctr++) {
                if (p.hasPermission("plotme.limit." + ctr)) {
                    max = ctr;
                }
            }

        }

        if (max == -2) {
            if (cPerms(p, "plotme.admin")) {
                return -1;
            } else if (cPerms(p, "plotme.use")) {
                return 1;
            } else {
                return 0;
            }
        }

        return max;
    }

    public static String getDate() {
        return getDate(Calendar.getInstance());
    }

    private static String getDate(final Calendar cal) {
        final int imonth = cal.get(Calendar.MONTH) + 1;
        final int iday = cal.get(Calendar.DAY_OF_MONTH) + 1;
        String month = "";
        String day = "";

        if (imonth < 10) {
            month = "0" + imonth;
        } else {
            month = "" + imonth;
        }

        if (iday < 10) {
            day = "0" + iday;
        } else {
            day = "" + iday;
        }

        return "" + cal.get(Calendar.YEAR) + "-" + month + "-" + day;
    }

    public static String getDate(final java.sql.Date expireddate) {
        return expireddate.toString();
    }

    public static boolean isIgnoringWELimit(final Player p) {
        if (defaultWEAnywhere && cPerms(p, "plotme.admin.weanywhere")) {
            return !playersignoringwelimit.contains(p.getName());
        } else {
            return playersignoringwelimit.contains(p.getName());
        }
    }

    public static void addIgnoreWELimit(final Player p) {
        if (!playersignoringwelimit.contains(p.getName())) {
            playersignoringwelimit.add(p.getName());
            if (worldeditplugin != null) {
                AthionPlots.athionworldedit.removeMask(p);
            }
        }
    }

    public static void removeIgnoreWELimit(final Player p) {
        if (playersignoringwelimit.contains(p.getName())) {
            playersignoringwelimit.remove(p.getName());
            if (worldeditplugin != null) {
                AthionPlots.athionworldedit.setMask(p);
            }
        }
    }

    public static String caption(final String s) {
        if (captions.containsKey(s)) {
            return addColor(captions.get(s));
        } else {
            logger.warning("Missing caption: " + s);
            return "ERROR:Missing caption '" + s + "'";
        }
    }

    public static String addColor(final String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static boolean cPerms(final CommandSender sender, final String node) {
        return sender.hasPermission(node);
    }

    @Override
    public void onEnable() {
        self = this;
        logger = getLogger();

        initialize();

        final PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new AthionListenerMain(), this);

        if (pm.getPlugin("WorldEdit") != null) {
            worldeditplugin = (WorldEditPlugin) pm.getPlugin("WorldEdit");

            try {
                Class.forName("com.sk89q.worldedit.function.mask.Mask");
                AthionPlots.athionworldedit = (AthionWorldEdit) Class.forName("net.athion.athionplots.WorldEdit.AthionWorldEdit6_0_0").getConstructor().newInstance();
            } catch (final Exception e) {
                try {
                    AthionPlots.athionworldedit = (AthionWorldEdit) Class.forName("net.athion.athionplots.WorldEdit.AthionWorldEdit5_7").getConstructor().newInstance();
                } catch (final Exception e2) {
                    logger.warning("Unable to hook to WorldEdit properly, please contact the developper of AthionPlots with your WorldEdit version.");
                    AthionPlots.athionworldedit = null;
                }
            }

            pm.registerEvents(new AthionListenerWE(), this);
        }

        if (allowToBlock) {
            pm.registerEvents(new AthionListenerBlock(), this);
        }
        final AthionCommands command = new AthionCommands(this);
        getCommand("ap").setExecutor(command);
        getCommand("plotme").setExecutor(command);
        getCommand("plots").setExecutor(command);
        getCommand("plot").setExecutor(command);

        initialized = true;

        AthionSQL.plotConvertToUUIDAsynchronously();

    }

    @Override
    public void onDisable() {
        AthionSQL.closeConnection();
        NAME = null;
        PREFIX = null;
        logger = null;
        usemySQL = null;
        mySQLuname = null;
        mySQLpass = null;
        mySQLconn = null;
        globalUseEconomy = null;
        advancedlogging = null;
        language = null;
        allowWorldTeleport = null;
        autoUpdate = null;
        AthionMaps = null;
        configpath = null;
        worldeditplugin = null;
        economy = null;
        usinglwc = null;
        playersignoringwelimit = null;
        captions = null;
        worldcurrentlyprocessingexpired = null;
        cscurrentlyprocessingexpired = null;
        counterexpired = null;
        nbperdeletionprocessingexpired = null;
        defaultWEAnywhere = null;
        self = null;
        allowToBlock = null;
        allowToMerge = null;
        allowToSchematic = null;
        initialized = null;
    }

    private void loadCaptions() {
        File filelang = new File(getDataFolder(), "plotme-english.yml");

        final TreeMap<String, String> properties = new TreeMap<String, String>();
        properties.put("MsgStartDeleteSession", "Starting to delete.");
        properties.put("MsgDeletedExpiredPlots", "The expired plot has been deleted.");
        properties.put("MsgDeleteSessionFinished", "Delete was succesful. Run again to delete more.");
        properties.put("MsgAlreadyProcessingPlots", "this is already being deleted.");
        properties.put("MsgDoesNotExistOrNotLoaded", "does not exist or is not loaded.");
        properties.put("MsgNotPlotWorld", "Hey, you're not in an AthionPlots world.");
        properties.put("MsgPermissionDenied", "Sorry, you don't have permission to do that.");
        properties.put("MsgNoPlotFound", "No Plots.");
        properties.put("MsgCannotBidOwnPlot", "You cannot bid on your own plot.");
        properties.put("MsgCannotBuyOwnPlot", "You cannot buy your own plot.");
        properties.put("MsgCannotClaimRoad", "You cannot claim the road.");
        properties.put("MsgInvalidBidMustBeAbove", "Invalid bid. Must be above");
        properties.put("MsgOutbidOnPlot", "Outbid on plot");
        properties.put("MsgOwnedBy", "plot owned by");
        properties.put("MsgBidAccepted", "Bid accepted.");
        properties.put("MsgPlotNotAuctionned", "This plot isn't being auctionned.");
        properties.put("MsgThisPlot", "This plot");
        properties.put("MsgThisPlotYours", "This plot is now yours.");
        properties.put("MsgThisPlotIsNow", "This plot is now ");
        properties.put("MsgThisPlotOwned", "This plot is already owned.");
        properties.put("MsgHasNoOwner", "has no owners.");
        properties.put("MsgEconomyDisabledWorld", "Economy is disabled for this world.");
        properties.put("MsgPlotNotForSale", "Plot isn't for sale.");
        properties.put("MsgAlreadyReachedMaxPlots", "You have already reached your maximum amount of plots");
        properties.put("MsgToGetToIt", "to get to it");
        properties.put("MsgNotEnoughBid", "You do not have enough to bid this much.");
        properties.put("MsgNotEnoughBuy", "You do not have enough to buy this plot.");
        properties.put("MsgNotEnoughAuto", "You do not have enough to buy a plot.");
        properties.put("MsgNotEnoughComment", "You do not have enough to comment on a plot.");
        properties.put("MsgNotEnoughBiome", "You do not have enough to change the biome.");
        properties.put("MsgNotEnoughClear", "You do not have enough to clear the plot.");
        properties.put("MsgNotEnoughDispose", "You do not have enough to dispose of this plot.");
        properties.put("MsgNotEnoughProtectPlot", "You do not have enough to protect this plot.");
        properties.put("MsgNotEnoughTp", "You do not have enough to teleport home.");
        properties.put("MsgNotEnoughAdd", "You do not have enough to add a player.");
        properties.put("MsgNotEnoughDeny", "You do not have enough to deny a player.");
        properties.put("MsgNotEnoughRemove", "You do not have enough to remove a player.");
        properties.put("MsgNotEnoughUndeny", "You do not have enough to undeny a player.");
        properties.put("MsgSoldTo", "sold to");
        properties.put("MsgPlotBought", "Plot bought.");
        properties.put("MsgBoughtPlot", "bought plot");
        properties.put("MsgClaimedPlot", "claimed plot");
        properties.put("MsgPlotHasBidsAskAdmin", "Plot is being auctionned and has bids. Ask an admin to cancel it.");
        properties.put("MsgAuctionCancelledOnPlot", "Auction cancelled on plot");
        properties.put("MsgAuctionCancelled", "Auction cancelled.");
        properties.put("MsgStoppedTheAuctionOnPlot", "stopped the auction on plot");
        properties.put("MsgInvalidAmount", "Invalid amount. Must be above or equal to 0.");
        properties.put("MsgAuctionStarted", "Auction started.");
        properties.put("MsgStartedAuctionOnPlot", "started an auction on plot");
        properties.put("MsgDoNotOwnPlot", "You do not own this plot.");
        properties.put("MsgSellingPlotsIsDisabledWorld", "Selling plots is disabled in this world.");
        properties.put("MsgPlotProtectedNotDisposed", "Plot is protected and cannot be disposed.");
        properties.put("MsgWasDisposed", "was disposed.");
        properties.put("MsgPlotDisposedAnyoneClaim", "Plot disposed. Anyone can claim it.");
        properties.put("MsgDisposedPlot", "disposed of plot");
        properties.put("MsgNotYoursCannotDispose", "is not yours. You are not allowed to dispose it.");
        properties.put("MsgPlotNoLongerSale", "Plot no longer for sale.");
        properties.put("MsgRemovedPlot", "removed the plot");
        properties.put("MsgFromBeingSold", "from being sold");
        properties.put("MsgCannotCustomPriceDefault", "You cannot customize the price. Default price is :");
        properties.put("MsgCannotSellToBank", "Plots cannot be sold to the bank in this world.");
        properties.put("MsgSoldToBank", "sold to bank.");
        properties.put("MsgPlotSold", "Plot sold.");
        properties.put("MsgSoldToBankPlot", "sold to bank plot");
        properties.put("MsgPlotForSale", "Plot now for sale.");
        properties.put("MsgPutOnSalePlot", "put on sale plot");
        properties.put("MsgPlotNoLongerProtected", "Plot is no longer protected. It is now possible to Clear or Reset it.");
        properties.put("MsgUnprotectedPlot", "unprotected plot");
        properties.put("MsgPlotNowProtected", "Plot is now protected. It won't be possible to Clear or Reset it.");
        properties.put("MsgProtectedPlot", "protected plot");
        properties.put("MsgNoPlotsFinished", "No plots are finished");
        properties.put("MsgFinishedPlotsPage", "Finished plots page");
        properties.put("MsgUnmarkFinished", "Plot is no longer marked finished.");
        properties.put("MsgMarkFinished", "Plot is now marked finished.");
        properties.put("MsgPlotExpirationReset", "Plot expiration reset");
        properties.put("MsgNoPlotExpired", "No plots are expired");
        properties.put("MsgExpiredPlotsPage", "Expired plots page");
        properties.put("MsgListOfPlotsWhere", "List of plots where");
        properties.put("MsgCanBuild", "can build:");
        properties.put("MsgListOfPlotsWhereYou", "List of plots where you can build:");
        properties.put("MsgWorldEditInYourPlots", "You can now only WorldEdit in your plots");
        properties.put("MsgWorldEditAnywhere", "You can now WorldEdit anywhere");
        properties.put("MsgNoPlotFound1", "No plot found within");
        properties.put("MsgNoPlotFound2", "plots. Contact an admin.");
        properties.put("MsgDoesNotHavePlot", "does not have a plot");
        properties.put("MsgPlotNotFound", "Could not find plot");
        properties.put("MsgYouHaveNoPlot", "You don't have a plot.");
        properties.put("MsgCommentAdded", "Comment added.");
        properties.put("MsgCommentedPlot", "commented on plot");
        properties.put("MsgNoComments", "No comments");
        properties.put("MsgYouHave", "You have");
        properties.put("MsgComments", "comments.");
        properties.put("MsgNotYoursNotAllowedViewComments", "is not yours. You are not allowed to view the comments.");
        properties.put("MsgIsInvalidBiome", "is not a valid biome.");
        properties.put("MsgBiomeSet", "Biome set to");
        properties.put("MsgChangedBiome", "changed the biome of plot");
        properties.put("MsgNotYoursNotAllowedBiome", "is not yours. You are not allowed to change it's biome.");
        properties.put("MsgPlotUsingBiome", "This plot is using the biome");
        properties.put("MsgPlotProtectedCannotReset", "Plot is protected and cannot be reset.");
        properties.put("MsgPlotProtectedCannotClear", "Plot is protected and cannot be cleared.");
        properties.put("MsgOwnedBy", "owned by");
        properties.put("MsgWasReset", "was reset.");
        properties.put("MsgPlotReset", "Plot has been reset.");
        properties.put("MsgResetPlot", "reset plot");
        properties.put("MsgPlotCleared", "Plot cleared.");
        properties.put("MsgClearedPlot", "cleared plot");
        properties.put("MsgNotYoursNotAllowedClear", "is not yours. You are not allowed to clear it.");
        properties.put("MsgAlreadyAllowed", "was already allowed");
        properties.put("MsgAlreadyDenied", "was already denied");
        properties.put("MsgWasNotAllowed", "was not allowed");
        properties.put("MsgWasNotDenied", "was not denied");
        properties.put("MsgNowUndenied", "now undenied.");
        properties.put("MsgNowDenied", "now denied.");
        properties.put("MsgNowAllowed", "now allowed.");
        properties.put("MsgAddedPlayer", "added player");
        properties.put("MsgDeniedPlayer", "denied player");
        properties.put("MsgRemovedPlayer", "removed player");
        properties.put("MsgUndeniedPlayer", "undenied player");
        properties.put("MsgToPlot", "to plot");
        properties.put("MsgFromPlot", "from plot");
        properties.put("MsgNotYoursNotAllowedAdd", "is not yours. You are not allowed to add someone to it.");
        properties.put("MsgNotYoursNotAllowedDeny", "is not yours. You are not allowed to deny someone from it.");
        properties.put("MsgNotYoursNotAllowedRemove", "is not yours. You are not allowed to remove someone from it.");
        properties.put("MsgNotYoursNotAllowedUndeny", "is not yours. You are not allowed to undeny someone to it.");
        properties.put("MsgNowOwnedBy", "is now owned by");
        properties.put("MsgChangedOwnerFrom", "changed owner from");
        properties.put("MsgChangedOwnerOf", "changed owner of");
        properties.put("MsgOwnerChangedTo", "Plot Owner has been set to");
        properties.put("MsgPlotMovedSuccess", "Plot moved successfully");
        properties.put("MsgExchangedPlot", "exchanged plot");
        properties.put("MsgAndPlot", "and plot");
        properties.put("MsgReloadedSuccess", "reloaded successfully");
        properties.put("MsgReloadedConfigurations", "reloaded configurations");
        properties.put("MsgNoPlotworldFound", "No Plot world found.");
        properties.put("MsgWorldNotPlot", "does not exist or is not a plot world.");
        properties.put("MsgCredit", "Credit: Empire92, Travis506, ZachBora, Athion.net");

        properties.put("ConsoleHelpMain", "[AthionPlots Console Help]");
        properties.put("ConsoleHelpReload", " - Reloads the plugin and its configuration files");

        properties.put("HelpTitle", "AthionPlots");

        properties.put("WordWorld", "World");
        properties.put("WordUsage", "Usage");
        properties.put("WordExample", "Example");
        properties.put("WordAmount", "amount");
        properties.put("WordUse", "Use");
        properties.put("WordPlot", "Plot");
        properties.put("WordFor", "for");
        properties.put("WordAt", "at");
        properties.put("WordMarked", "marked");
        properties.put("WordFinished", "finished");
        properties.put("WordUnfinished", "unfinished");
        properties.put("WordAuction", "Auction");
        properties.put("WordSell", "Sell");
        properties.put("WordYours", "Yours");
        properties.put("WordHelpers", "Helpers");
        properties.put("WordInfinite", "Infinite");
        properties.put("WordPrice", "Price");
        properties.put("WordPlayer", "Player");
        properties.put("WordComment", "comment");
        properties.put("WordBiome", "biome");
        properties.put("WordId", "id");
        properties.put("WordIdFrom", "id-from");
        properties.put("WordIdTo", "id-to");
        properties.put("WordNever", "Never");
        properties.put("WordDefault", "Default");
        properties.put("WordMissing", "Missing");
        properties.put("WordYes", "Yes");
        properties.put("WordNo", "No");
        properties.put("WordText", "text");
        properties.put("WordFrom", "From");
        properties.put("WordTo", "to");
        properties.put("WordBiomes", "Biomes");
        properties.put("WordNotApplicable", "N/A");
        properties.put("WordBottom", "Bottom");
        properties.put("WordTop", "Top");
        properties.put("WordPossessive", "'s");
        properties.put("WordRemoved", "removed");

        properties.put("SignOwner", "Owner:");
        properties.put("SignId", "ID:");
        properties.put("SignForSale", "&9&lFOR SALE");
        properties.put("SignPrice", "Price :");
        properties.put("SignPriceColor", "&9");
        properties.put("SignOnAuction", "&9&lON AUCTION");
        properties.put("SignMinimumBid", "Minimum bid :");
        properties.put("SignCurrentBid", "Current bid :");
        properties.put("SignCurrentBidColor", "&9");

        properties.put("InfoId", "ID");
        properties.put("InfoOwner", "Owner");
        properties.put("InfoBiome", "Biome");
        properties.put("InfoExpire", "Expire date");
        properties.put("InfoFinished", "Finished");
        properties.put("InfoProtected", "Protected");
        properties.put("InfoHelpers", "Helpers");
        properties.put("InfoDenied", "Denied");
        properties.put("InfoAuctionned", "Auctionned");
        properties.put("InfoBidder", "Bidder");
        properties.put("InfoBid", "Bid");
        properties.put("InfoForSale", "For sale");
        properties.put("InfoMinimumBid", "Minimum bid");

        properties.put("CommandBuy", "buy");
        properties.put("CommandBid", "bid");
        properties.put("CommandResetExpired", "resetexpired");
        properties.put("CommandHelp", "help");
        properties.put("CommandClaim", "claim");
        properties.put("CommandAuto", "auto");
        properties.put("CommandInfo", "info");
        properties.put("CommandComment", "comment");
        properties.put("CommandComments", "comments");
        properties.put("CommandBiome", "biome");
        properties.put("CommandBiomelist", "biomelist");
        properties.put("CommandId", "id");
        properties.put("CommandTp", "tp");
        properties.put("CommandClear", "clear");
        properties.put("CommandReset", "reset");
        properties.put("CommandAdd", "add");
        properties.put("CommandDeny", "deny");
        properties.put("CommandRemove", "remove");
        properties.put("CommandUndeny", "undeny");
        properties.put("CommandSetowner", "setowner");
        properties.put("CommandMove", "move");
        properties.put("CommandWEAnywhere", "weanywhere");
        properties.put("CommandList", "list");
        properties.put("CommandExpired", "expired");
        properties.put("CommandAddtime", "addtime");
        properties.put("CommandDone", "done");
        properties.put("CommandDoneList", "donelist");
        properties.put("CommandProtect", "protect");
        properties.put("CommandSell", "sell");
        properties.put("CommandSellBank", "sell bank");
        properties.put("CommandDispose", "dispose");
        properties.put("CommandAuction", "auction");
        properties.put("CommandHome", "home");

        properties.put("ErrCannotBuild", "You cannot build here.");
        properties.put("ErrCannotUseEggs", "You cannot use eggs here.");
        properties.put("ErrCannotUse", "You cannot use that.");
        properties.put("ErrCreatingPlotAt", "An error occured while creating the plot at");
        properties.put("ErrMovingPlot", "Error moving plot");

        CreateConfig(filelang, properties, "AthionPlots Caption configuration αω");

        if (language != "english") {
            filelang = new File(getDataFolder(), "plotme-english.yml");
            CreateConfig(filelang, properties, "AthionPlots Caption configuration");
        }

        InputStream input = null;

        try {
            input = new FileInputStream(filelang);
            final Yaml yaml = new Yaml();
            final Object obj = yaml.load(input);

            if (obj instanceof LinkedHashMap<?, ?>) {
                @SuppressWarnings("unchecked")
                final LinkedHashMap<String, String> data = (LinkedHashMap<String, String>) obj;

                captions = new HashMap<String, String>();
                for (final String key : data.keySet()) {
                    captions.put(key, data.get(key));
                }
            }
        } catch (final FileNotFoundException e) {
            logger.severe("File not found: " + e.getMessage());
            e.printStackTrace();
        } catch (final Exception e) {
            logger.severe("Error with configuration: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (final IOException e) {}
            }
        }
    }

    private void CreateConfig(final File file, final TreeMap<String, String> properties, final String Title) {
        if (!file.exists()) {
            BufferedWriter writer = null;

            try {
                final File dir = new File(getDataFolder(), "");
                dir.mkdirs();

                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
                writer.write("# " + Title + "\n");

                for (final Entry<String, String> e : properties.entrySet()) {
                    writer.write(e.getKey() + ": '" + e.getValue().replace("'", "''") + "'\n");
                }

                writer.close();
            } catch (final IOException e) {
                logger.severe("Unable to create config file : " + Title + "!");
                logger.severe(e.getMessage());
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (final IOException e2) {}
                }
            }
        } else {
            OutputStreamWriter writer = null;
            InputStream input = null;

            try {
                input = new FileInputStream(file);
                final Yaml yaml = new Yaml();
                final Object obj = yaml.load(input);

                if (obj instanceof LinkedHashMap<?, ?>) {
                    @SuppressWarnings("unchecked")
                    final LinkedHashMap<String, String> data = (LinkedHashMap<String, String>) obj;

                    writer = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");

                    for (final Entry<String, String> e : properties.entrySet()) {
                        if (!data.containsKey(e.getKey())) {
                            writer.write("\n" + e.getKey() + ": '" + e.getValue().replace("'", "''") + "'");
                        }
                    }

                    writer.close();
                    input.close();
                }
            } catch (final FileNotFoundException e) {
                logger.severe("File not found: " + e.getMessage());
                e.printStackTrace();
            } catch (final Exception e) {
                logger.severe("Error with configuration: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (final IOException e2) {}
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (final IOException e) {}
                }
            }
        }
    }

    public void scheduleTask(final Runnable task, final int eachseconds, final int howmanytimes) {
        AthionPlots.cscurrentlyprocessingexpired.sendMessage("" + AthionPlots.PREFIX + ChatColor.RESET);

        for (int ctr = 0; ctr < (howmanytimes / nbperdeletionprocessingexpired); ctr++) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, task, ctr * eachseconds * 20);
        }
    }

    private short getBlockId(final ConfigurationSection cs, final String section, final String def) {
        final String idvalue = cs.getString(section, def.toString());
        if (idvalue.indexOf(":") > 0) {
            return Short.parseShort(idvalue.split(":")[0]);
        } else {
            return Short.parseShort(idvalue);
        }
    }

    private byte getBlockValue(final ConfigurationSection cs, final String section, final String def) {
        final String idvalue = cs.getString(section, def.toString());
        if (idvalue.indexOf(":") > 0) {
            return Byte.parseByte(idvalue.split(":")[1]);
        } else {
            return 0;
        }
    }

    private String getBlockValueId(final Short id, final Byte value) {
        return (value == 0) ? id.toString() : id.toString() + ":" + value.toString();
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(final String worldName, final String id) {
        if (AthionCore.isPlotWorld(worldName)) {
            // Configuration found for this worldName, creating the world
            // with the settings for this world.
            return new AthionGen(AthionCore.getMap(worldName));
        } else {
            // Configuration could not be found, or the world config information was not found.
            // Resulting in creation by default
            return new AthionGen();
        }
    }

}
