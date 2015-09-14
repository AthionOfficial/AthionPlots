package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandTeleport {

    public CommandTeleport(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "AthionPlots.admin.tp")) {
            if (!AthionCore.isPlotWorld(p)) {
                if (!AthionPlots.allowWorldTeleport) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
                } else if ((args.length == 2) || (args.length == 3)) {
                    final String id = args[1];

                    if (!AthionCore.isValidId(id)) {
                        AthionCommands.SendMsg(p, AthionCommands.C("WordUsage")
                        + ": "
                        + ChatColor.RED
                        + "/ap "
                        + AthionCommands.C("CommandTp")
                        + " <"
                        + AthionCommands.C("WordId")
                        + "> ["
                        + AthionCommands.C("WordWorld")
                        + "] "
                        + ChatColor.RESET
                        + AthionCommands.C("WordExample")
                        + ": "
                        + ChatColor.RED
                        + "/ap "
                        + AthionCommands.C("CommandTp")
                        + " 5;-1 ");

                    } else {
                        World w;

                        if (args.length == 3) {
                            final String world = args[2];

                            w = Bukkit.getWorld(world);
                        } else if (!AthionCore.isPlotWorld(p)) {
                            w = AthionCore.getFirstWorld();
                        } else {
                            w = p.getWorld();
                        }

                        if ((w == null) || !AthionCore.isPlotWorld(w)) {
                            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotworldFound"));
                        } else {
                            final Location bottom = AthionCore.getPlotBottomLoc(w, id);
                            final Location top = AthionCore.getPlotTopLoc(w, id);

                            p.teleport(new Location(w, bottom.getX() + ((top.getBlockX() - bottom.getBlockX()) / 2), AthionCore.getMap(w).RoadHeight + 2, bottom.getZ() - 2));
                        }
                    }
                } else {
                    AthionCommands.SendMsg(p, AthionCommands.C("WordUsage")
                    + ": "
                    + ChatColor.RED
                    + "/ap "
                    + AthionCommands.C("CommandTp")
                    + " <"
                    + AthionCommands.C("WordId")
                    + "> ["
                    + AthionCommands.C("WordWorld")
                    + "] "
                    + ChatColor.RESET
                    + AthionCommands.C("WordExample")
                    + ": "
                    + ChatColor.RED
                    + "/ap "
                    + AthionCommands.C("CommandTp")
                    + " 5;-1 ");
                }
            } else if ((args.length == 2) || ((args.length == 3) && AthionPlots.allowWorldTeleport)) {
                final String id = args[1];

                if (!AthionCore.isValidId(id)) {
                    if (AthionPlots.allowWorldTeleport) {
                        AthionCommands.SendMsg(p, AthionCommands.C("WordUsage")
                        + ": "
                        + ChatColor.RED
                        + "/ap "
                        + AthionCommands.C("CommandTp")
                        + " <"
                        + AthionCommands.C("WordId")
                        + "> ["
                        + AthionCommands.C("WordWorld")
                        + "] "
                        + ChatColor.RESET
                        + AthionCommands.C("WordExample")
                        + ": "
                        + ChatColor.RED
                        + "/ap "
                        + AthionCommands.C("CommandTp")
                        + " 5;-1 ");
                    } else {
                        AthionCommands.SendMsg(p, AthionCommands.C("WordUsage")
                        + ": "
                        + ChatColor.RED
                        + "/ap "
                        + AthionCommands.C("CommandTp")
                        + " <"
                        + AthionCommands.C("WordId")
                        + "> "
                        + ChatColor.RESET
                        + AthionCommands.C("WordExample")
                        + ": "
                        + ChatColor.RED
                        + "/ap "
                        + AthionCommands.C("CommandTp")
                        + " 5;-1 ");
                    }

                } else {
                    World w;

                    if (args.length == 3) {
                        final String world = args[2];

                        w = Bukkit.getWorld(world);
                    } else if (!AthionCore.isPlotWorld(p)) {
                        w = AthionCore.getFirstWorld();
                    } else {
                        w = p.getWorld();
                    }

                    if ((w == null) || !AthionCore.isPlotWorld(w)) {
                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotworldFound"));
                    } else {
                        final Location bottom = AthionCore.getPlotBottomLoc(w, id);
                        final Location top = AthionCore.getPlotTopLoc(w, id);

                        p.teleport(new Location(w, bottom.getX() + ((top.getBlockX() - bottom.getBlockX()) / 2), AthionCore.getMap(w).RoadHeight + 2, bottom.getZ() - 2));
                    }
                }
            } else if (AthionPlots.allowWorldTeleport) {
                AthionCommands.SendMsg(p,
                AthionCommands.C("WordUsage")
                + ": "
                + ChatColor.RED
                + "/ap "
                + AthionCommands.C("CommandTp")
                + " <"
                + AthionCommands.C("WordId")
                + "> ["
                + AthionCommands.C("WordWorld")
                + "] "
                + ChatColor.RESET
                + AthionCommands.C("WordExample")
                + ": "
                + ChatColor.RED
                + "/ap "
                + AthionCommands.C("CommandTp")
                + " 5;-1 ");
            } else {
                AthionCommands.SendMsg(p, AthionCommands.C("WordUsage")
                + ": "
                + ChatColor.RED
                + "/ap "
                + AthionCommands.C("CommandTp")
                + " <"
                + AthionCommands.C("WordId")
                + "> "
                + ChatColor.RESET
                + AthionCommands.C("WordExample")
                + ": "
                + ChatColor.RED
                + "/ap "
                + AthionCommands.C("CommandTp")
                + " 5;-1 ");
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
