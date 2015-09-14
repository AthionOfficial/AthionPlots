package net.athion.athionplots.Commands;

import java.util.UUID;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionPlot;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandUnblock {

    public CommandUnblock(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "AthionPlots.admin.unblock") || AthionPlots.cPerms(p, "AthionPlots.use.unblock")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
            } else {
                final String id = AthionCore.getPlotID(p.getLocation());
                if (id.equals("")) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else if (!AthionCore.isPlotAvailable(id, p)) {
                    if ((args.length < 2) || args[1].equalsIgnoreCase("")) {
                        AthionCommands.SendMsg(p, AthionCommands.C("WordUsage") + ": " + ChatColor.RED + "/ap " + AthionCommands.C("CommandUndeny") + " <" + AthionCommands.C("WordPlayer") + ">");
                    } else {
                        final AthionPlot plot = AthionCore.getPlotById(p, id);
                        p.getName();
                        final UUID playeruuid = p.getUniqueId();
                        final String denied = args[1];

                        if (plot.ownerId.equals(playeruuid) || AthionPlots.cPerms(p, "AthionPlots.admin.unblock")) {
                            if (plot.isDeniedConsulting(denied) || plot.isGroupDenied(denied)) {
                                final World w = p.getWorld();

                                final AthionMaps pmi = AthionCore.getMap(w);

                                double price = 0;

                                if (AthionCore.isEconomyEnabled(w)) {
                                    price = pmi.UndenyPlayerPrice;
                                    final double balance = AthionPlots.economy.getBalance(p);

                                    if (balance >= price) {
                                        final EconomyResponse er = AthionPlots.economy.withdrawPlayer(p, price);

                                        if (!er.transactionSuccess()) {
                                            AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                            AthionCommands.warn(er.errorMessage);

                                        }
                                    } else {
                                        AthionCommands.SendMsg(p, ChatColor.RED
                                        + AthionCommands.C("MsgNotEnoughUndeny")
                                        + " "
                                        + AthionCommands.C("WordMissing")
                                        + " "
                                        + ChatColor.RESET
                                        + AthionCommands.f(price - balance, false));

                                    }
                                }

                                if (denied.startsWith("group:")) {
                                    plot.removeDeniedGroup(denied);
                                } else {
                                    plot.removeDenied(denied);
                                }

                                AthionCommands.SendMsg(p, AthionCommands.C("WordPlayer")
                                + " "
                                + ChatColor.RED
                                + denied
                                + ChatColor.RESET
                                + " "
                                + AthionCommands.C("MsgNowUndenied")
                                + " "
                                + AthionCommands.f(-price));

                            } else {
                                AthionCommands.SendMsg(p, AthionCommands.C("WordPlayer") + " " + ChatColor.RED + args[1] + ChatColor.RESET + " " + AthionCommands.C("MsgWasNotDenied"));
                            }
                        } else {
                            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgThisPlot") + "(" + id + ") " + AthionCommands.C("MsgNotYoursNotAllowedUndeny"));
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
