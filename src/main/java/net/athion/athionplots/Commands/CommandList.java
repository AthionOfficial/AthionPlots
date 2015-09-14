package net.athion.athionplots.Commands;

import java.util.Calendar;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionPlot;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandList {

    public CommandList(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "plotme.use.list")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));

            } else {
                String name;
                final String pname = p.getName();

                if (AthionPlots.cPerms(p, "plotme.admin.list") && (args.length == 2)) {
                    name = args[1];
                    AthionCommands.SendMsg(p, AthionCommands.C("MsgListOfPlotsWhere") + " " + ChatColor.BLUE + name + ChatColor.RESET + " " + AthionCommands.C("MsgCanBuild"));
                } else {
                    name = p.getName();
                    AthionCommands.SendMsg(p, AthionCommands.C("MsgListOfPlotsWhereYou"));
                }

                for (final AthionPlot plot : AthionCore.getPlots(p).values()) {
                    final StringBuilder addition = new StringBuilder();

                    if (plot.expireddate != null) {
                        final java.util.Date tempdate = plot.expireddate;

                        if (tempdate.compareTo(Calendar.getInstance().getTime()) < 0) {
                            addition.append(ChatColor.RED + " -> " + plot.expireddate.toString() + ChatColor.RESET);
                        } else {
                            addition.append(" -> " + plot.expireddate.toString());
                        }
                    }

                    if (plot.auctionned) {
                        addition.append(" "
                        + AthionCommands.C("WordAuction")
                        + ": "
                        + ChatColor.GREEN
                        + AthionCommands.round(plot.currentbid)
                        + ChatColor.RESET
                        + (((plot.currentbidder != null) && !plot.currentbidder.equals("")) ? " " + plot.currentbidder : ""));
                    }

                    if (plot.forsale) {
                        addition.append(" " + AthionCommands.C("WordSell") + ": " + ChatColor.GREEN + AthionCommands.round(plot.customprice) + ChatColor.RESET);
                    }

                    if (plot.owner.equalsIgnoreCase(name)) {
                        if (plot.allowedcount() == 0) {
                            if (name.equalsIgnoreCase(pname)) {
                                p.sendMessage("Plot: "
                                + ChatColor.GRAY
                                + plot.id
                                + " -> "
                                + ChatColor.BLUE
                                + ChatColor.ITALIC
                                + /*AthionCommands.C("WordYours")*/ChatColor.YELLOW
                                + "Claimed On"
                                + ChatColor.GRAY
                                + addition);
                            } else {
                                p.sendMessage("Plot: " + ChatColor.GRAY + plot.id + " -> " + ChatColor.BLUE + ChatColor.ITALIC + plot.owner + ChatColor.RESET + addition);
                            }
                        } else {
                            final StringBuilder helpers = new StringBuilder();
                            for (int i = 0; i < plot.allowedcount(); i++) {
                                helpers.append(ChatColor.BLUE).append(plot.allowed().toArray()[i]).append(ChatColor.RESET).append(", ");
                            }
                            if (helpers.length() > 2) {
                                helpers.delete(helpers.length() - 2, helpers.length());
                            }

                            if (name.equalsIgnoreCase(pname)) {
                                p.sendMessage("Plot: "
                                + ChatColor.GRAY
                                + plot.id
                                + " -> "
                                + ChatColor.BLUE
                                + ChatColor.ITALIC
                                + /*AthionCommands.C("WordYours")*/ChatColor.YELLOW
                                + "Claimed On"
                                + ChatColor.GRAY
                                + addition
                                + ", "
                                + AthionCommands.C("WordHelpers")
                                + ": "
                                + helpers);
                            } else {
                                p.sendMessage("Plot: "
                                + ChatColor.GRAY
                                + plot.id
                                + " -> "
                                + ChatColor.BLUE
                                + ChatColor.ITALIC
                                + plot.owner
                                + ChatColor.RESET
                                + addition
                                + ", "
                                + AthionCommands.C("WordHelpers")
                                + ": "
                                + helpers);
                            }
                        }
                    } else if (plot.isAllowedConsulting(name)) {
                        final StringBuilder helpers = new StringBuilder();
                        for (int i = 0; i < plot.allowedcount(); i++) {
                            if (p.getName().equalsIgnoreCase((String) plot.allowed().toArray()[i])) {
                                if (name.equalsIgnoreCase(pname)) {
                                    helpers.append(ChatColor.BLUE).append(ChatColor.ITALIC).append("You").append(ChatColor.RESET).append(", ");
                                } else {
                                    helpers.append(ChatColor.BLUE).append(ChatColor.ITALIC).append(args[1]).append(ChatColor.RESET).append(", ");
                                }
                            } else {
                                helpers.append(ChatColor.BLUE).append(plot.allowed().toArray()[i]).append(ChatColor.RESET).append(", ");
                            }
                        }
                        if (helpers.length() > 2) {
                            helpers.delete(helpers.length() - 2, helpers.length());
                        }

                        if (plot.owner.equalsIgnoreCase(pname)) {
                            p.sendMessage("Plot: "
                            + ChatColor.GRAY
                            + plot.id
                            + " -> "
                            + ChatColor.BLUE
                            + /*AthionCommands.C("WordYours")*/ChatColor.YELLOW
                            + "Claimed On"
                            + ChatColor.GRAY
                            + addition
                            + ", "
                            + AthionCommands.C("WordHelpers")
                            + ": "
                            + helpers);
                        } else {
                            p.sendMessage("Plot: "
                            + ChatColor.GRAY
                            + plot.id
                            + " -> "
                            + ChatColor.BLUE
                            + plot.owner
                            + AthionCommands.C("WordPossessive")
                            + ChatColor.RESET
                            + addition
                            + ", "
                            + AthionCommands.C("WordHelpers")
                            + ": "
                            + helpers);
                        }
                    }
                }
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
