package net.athion.athionplots.Core;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Commands.CommandAddPlayer;
import net.athion.athionplots.Commands.CommandAddTime;
import net.athion.athionplots.Commands.CommandAuction;
import net.athion.athionplots.Commands.CommandAutoClaim;
import net.athion.athionplots.Commands.CommandBid;
import net.athion.athionplots.Commands.CommandBiome;
import net.athion.athionplots.Commands.CommandBiomeList;
import net.athion.athionplots.Commands.CommandBlock;
import net.athion.athionplots.Commands.CommandBuy;
import net.athion.athionplots.Commands.CommandClaim;
import net.athion.athionplots.Commands.CommandClear;
import net.athion.athionplots.Commands.CommandComment;
import net.athion.athionplots.Commands.CommandComments;
import net.athion.athionplots.Commands.CommandDispose;
import net.athion.athionplots.Commands.CommandDone;
import net.athion.athionplots.Commands.CommandDoneList;
import net.athion.athionplots.Commands.CommandExpired;
import net.athion.athionplots.Commands.CommandHelp;
import net.athion.athionplots.Commands.CommandHome;
import net.athion.athionplots.Commands.CommandID;
import net.athion.athionplots.Commands.CommandInfo;
import net.athion.athionplots.Commands.CommandList;
import net.athion.athionplots.Commands.CommandMerge;
import net.athion.athionplots.Commands.CommandMove;
import net.athion.athionplots.Commands.CommandPlugin;
import net.athion.athionplots.Commands.CommandProtect;
import net.athion.athionplots.Commands.CommandReload;
import net.athion.athionplots.Commands.CommandRemove;
import net.athion.athionplots.Commands.CommandReset;
import net.athion.athionplots.Commands.CommandResetExpiredPlot;
import net.athion.athionplots.Commands.CommandSchematic;
import net.athion.athionplots.Commands.CommandSell;
import net.athion.athionplots.Commands.CommandSetOwner;
import net.athion.athionplots.Commands.CommandTeleport;
import net.athion.athionplots.Commands.CommandUnMerge;
import net.athion.athionplots.Commands.CommandUnblock;
import net.athion.athionplots.Commands.CommandWorldEditAnywhere;
import net.athion.athionplots.Utils.MFWC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AthionCommands implements CommandExecutor {

    public final static String SYSTEM_PREFIX = AthionPlots.PREFIX;
    private final static String LOG = "[" + AthionPlots.NAME + " Event] ";
    private final AthionPlots plugin;

    public AthionCommands(final AthionPlots instance) {
        plugin = instance;
    }

    public static StringBuilder whitespace(final int length) {
        final int spaceWidth = MFWC.getStringWidth(" ");

        final StringBuilder ret = new StringBuilder();

        for (int i = 0; (i + spaceWidth) < length; i += spaceWidth) {
            ret.append(" ");
        }

        return ret;
    }

    public static String round(final double money) {
        return ((money % 1) == 0) ? "" + Math.round(money) : "" + money;
    }

    public static void warn(final String msg) {
        AthionPlots.logger.warning(LOG + msg);
    }

    public static String f(final double price) {
        return AthionCommands.f(price, true);
    }

    public static String f(final double price, final boolean showsign) {
        if (price == 0) {
            return "";
        }

        String format = round(Math.abs(price));

        if (AthionPlots.economy != null) {
            format = ((price <= 1) && (price >= -1)) ? format + " " + AthionPlots.economy.currencyNameSingular() : format + " " + AthionPlots.economy.currencyNamePlural();
        }

        if (showsign) {
            return ChatColor.GREEN + ((price > 0) ? "+" + format : "-" + format);
        } else {
            return ChatColor.GREEN + format;
        }
    }

    public static String C(final String caption) {
        return AthionPlots.caption(caption);
    }

    public static void SendMsg(final CommandSender cs, final String text) {
        cs.sendMessage(AthionCommands.SYSTEM_PREFIX + text);
    }

    public static String FormatBiome(String biome) {
        biome = biome.toLowerCase();

        final String[] tokens = biome.split("_");

        biome = "";

        for (String token : tokens) {
            token = token.substring(0, 1).toUpperCase() + token.substring(1);

            if (biome.equalsIgnoreCase("")) {
                biome = token;
            } else {
                biome = biome + "_" + token;
            }
        }

        return biome;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String l, final String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player) sender;
            if (args.length == 0) {
                new CommandHelp(p, 1);
                return true;
            }

            final String a0 = args[0];

            AthionMaps pmi = AthionCore.getMap(p);
            if (pmi == null) {
                pmi = AthionCore.getMap(AthionCore.getFirstWorld()); 
            }
            final int limit = pmi.PlotAutoLimit;

            if (a0.startsWith("test")) {
                Bukkit.broadcastMessage("Limit: " + limit);
                return true;
            }
            if (a0.startsWith("claim")) {
                new CommandClaim(p, args);
                return true;
            }
            if (a0.startsWith("plugin")) {
                new CommandPlugin(p, args);
                return true;
            }
            if (a0.startsWith("auto")) {
                new CommandAutoClaim(p, args);
                return true;
            }
            if (a0.startsWith("info") || a0.startsWith("i")) {
                new CommandInfo(p, args);
                return true;
            }
            if (a0.startsWith("comment")) {
                new CommandComment(p, args);
                return true;
            }
            if (a0.startsWith("comments")) {
                new CommandComments(p, args);
                return true;
            }
            if (a0.startsWith("biome")) {
                new CommandBiome(p, args);
                return true;
            }
            if (a0.startsWith("biomelist")) {
                new CommandBiomeList(p, args);
                return true;
            }
            if (a0.startsWith("id")) {
                new CommandID(p, args);
                return true;
            }
            if (a0.startsWith("tp")) {
                new CommandTeleport(p, args);
                return true;
            }
            if (a0.startsWith("clear")) {
                new CommandClear(p, args);
                return true;
            }
            if (a0.startsWith("reset")) {
                new CommandReset(p, args);
                return true;
            }
            if (a0.startsWith("add")) {
                new CommandAddPlayer(p, args);
                return true;
            }
            if (a0.startsWith("block")) {
                if (AthionPlots.allowToBlock) {
                    new CommandBlock(p, args);
                }
                return true;
            }
            if (a0.startsWith("unblock")) {
                if (AthionPlots.allowToBlock) {
                    new CommandUnblock(p, args);
                }
                return true;
            }
            if (a0.startsWith("remove")) {
                new CommandRemove(p, args);
                return true;
            }
            if (a0.startsWith("setowner")) {
                new CommandSetOwner(p, args);
                return true;
            }
            if (a0.startsWith("move")) {
                new CommandMove(p, args);
                return true;
            }
            if (a0.startsWith("reload")) {
                new CommandReload(p, args);
                return true;
            }
            if (a0.startsWith("weanywhere")) {
                new CommandWorldEditAnywhere(p, args);
                return true;
            }
            if (a0.startsWith("list")) {
                new CommandList(p, args);
                return true;
            }
            if (a0.startsWith("expired")) {
                new CommandExpired(p, args);
                return true;
            }
            if (a0.startsWith("addtime")) {
                new CommandAddTime(p, args);
                return true;
            }
            if (a0.startsWith("done")) {
                new CommandDone(p, args);
                return true;
            }
            if (a0.startsWith("donelist")) {
                new CommandDoneList(p, args);
                return true;
            }
            if (a0.startsWith("protect")) {
                new CommandProtect(p, args);
                return true;
            }
            if (a0.startsWith("sell")) {
                new CommandSell(p, args);
                return true;
            }
            if (a0.startsWith("dispose")) {
                new CommandDispose(p, args);
                return true;
            }
            if (a0.startsWith("auction")) {
                new CommandAuction(p, args);
                return true;
            }
            if (a0.startsWith("buy")) {
                new CommandBuy(p, args);
                return true;
            }
            if (a0.startsWith("bid")) {
                new CommandBid(p, args);
                return true;
            }
            if (a0.startsWith("resetexpired")) {
                new CommandResetExpiredPlot(p, args);
                return true;
            }
            if (a0.startsWith("merge")) {
                if (AthionPlots.allowToMerge) {
                    new CommandMerge(p, args);
                }
                return true;
            }
            // Disabled for not being fully developed.
            // Source for the current development has been placed in,
            // CommandUnMerge.java
            // AthionCore.java
            if (a0.startsWith("unmerge")) {
                new CommandUnMerge(p, args);
                return true;
            }
            if (a0.startsWith("schematic")) {
                new CommandSchematic(p, args, plugin);
                return true;
            }
            if (a0.startsWith("home")) {
                new CommandHome(p, args);
                return true;
            }
            if (args.length == 2) {
                int page = -1;
                try {
                    page = Integer.parseInt(args[1]);
                } catch (final NumberFormatException ignored) {}
                if (page != -1) {
                    new CommandHelp(p, page);
                } else {
                    new CommandHelp(p, 1);
                }
            } else {
                new CommandHelp(p, 1);
            }
        }

        return true;
    }

}
