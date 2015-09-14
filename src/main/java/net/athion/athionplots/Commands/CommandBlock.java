package net.athion.athionplots.Commands;

import java.util.List;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionPlot;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandBlock {

    public CommandBlock(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "plotme.admin.block") || AthionPlots.cPerms(p, "plotme.use.block")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
            } else {
                final String id = AthionCore.getPlotID(p.getLocation());
                if (id.equals("")) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else if (!AthionCore.isPlotAvailable(id, p)) {
                    if ((args.length < 2) || args[1].equalsIgnoreCase("")) {
                        AthionCommands.SendMsg(p, AthionCommands.C("WordUsage") + " " + ChatColor.RED + "/ap " + AthionCommands.C("CommandDeny") + " <" + AthionCommands.C("WordPlayer") + ">");
                    } else {

                        final AthionPlot plot = AthionCore.getPlotById(p, id);
                        final String playername = p.getName();
                        final String denied = args[1];

                        if (plot.owner.equalsIgnoreCase(playername) || AthionPlots.cPerms(p, "plotme.admin.block")) {
                            if (plot.isDeniedConsulting(denied) || plot.isGroupDenied(denied)) {
                                AthionCommands.SendMsg(p, AthionCommands.C("WordPlayer") + " " + ChatColor.RED + args[1] + ChatColor.RESET + " " + AthionCommands.C("MsgAlreadyDenied"));
                            } else {
                                final World w = p.getWorld();

                                final AthionMaps pmi = AthionCore.getMap(w);

                                double price = 0;

                                if (AthionCore.isEconomyEnabled(w)) {
                                    price = pmi.DenyPlayerPrice;
                                    final double balance = AthionPlots.economy.getBalance(p);

                                    if (balance >= price) {
                                        final EconomyResponse er = AthionPlots.economy.withdrawPlayer(p, price);

                                        if (!er.transactionSuccess()) {
                                            AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                            AthionCommands.warn(er.errorMessage);

                                        }
                                    } else {
                                        AthionCommands
                                        .SendMsg(p,
                                        ChatColor.RED + AthionCommands.C("MsgNotEnoughDeny") + " " + AthionCommands.C("WordMissing") + " " + ChatColor.RESET + AthionCommands.f(price - balance, false));

                                    }
                                }

                                plot.addDenied(denied);
                                plot.removeAllowed(denied);

                                if (denied.equals("*")) {
                                    final List<Player> deniedplayers = AthionCore.getPlayersInPlot(w, id);

                                    for (final Player deniedplayer : deniedplayers) {
                                        if (!plot.isAllowed(deniedplayer.getUniqueId())) {
                                            deniedplayer.teleport(AthionCore.getPlotHome(w, plot));
                                        }
                                    }
                                } else {
                                    @SuppressWarnings("deprecation")
                                    final Player deniedplayer = Bukkit.getPlayerExact(denied);

                                    if (deniedplayer != null) {
                                        if (deniedplayer.getWorld().equals(w)) {
                                            final String deniedid = AthionCore.getPlotId(deniedplayer);

                                            if (deniedid.equalsIgnoreCase(id)) {
                                                deniedplayer.teleport(AthionCore.getPlotHome(w, plot));
                                            }
                                        }
                                    }
                                }

                                AthionCommands.SendMsg(p, AthionCommands.C("WordPlayer")
                                + " "
                                + ChatColor.RED
                                + denied
                                + ChatColor.RESET
                                + " "
                                + AthionCommands.C("MsgNowDenied")
                                + " "
                                + AthionCommands.f(-price));

                            }
                        } else {
                            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgThisPlot") + "(" + id + ") " + AthionCommands.C("MsgNotYoursNotAllowedDeny"));
                        }
                    }
                } else {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgThisPlot") + "(" + id + ") " + AthionCommands.C("MsgHasNoOwner"));
                }
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
