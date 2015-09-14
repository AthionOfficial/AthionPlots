package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionPlot;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandAddPlayer {

    public CommandAddPlayer(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "plotme.admin.add") || AthionPlots.cPerms(p, "plotme.use.add")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
            } else {
                final String id = AthionCore.getPlotID(p.getLocation());
                if (id.equals("")) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else if (!AthionCore.isPlotAvailable(id, p)) {
                    if ((args.length < 2) || args[1].equalsIgnoreCase("")) {
                        AthionCommands.SendMsg(p, AthionCommands.C("WordUsage") + " " + ChatColor.RED + "/ap " + AthionCommands.C("CommandAdd") + " <" + AthionCommands.C("WordPlayer") + ">");
                    } else {

                        final AthionPlot plot = AthionCore.getPlotById(p, id);
                        final String playername = p.getName();
                        final String allowed = args[1];

                        if (plot.owner.equalsIgnoreCase(playername) || AthionPlots.cPerms(p, "plotme.admin.add")) {
                            if (plot.isAllowedConsulting(allowed) || plot.isGroupAllowed(allowed)) {
                                AthionCommands.SendMsg(p, AthionCommands.C("WordPlayer") + " " + ChatColor.RED + args[1] + ChatColor.RESET + " " + AthionCommands.C("MsgAlreadyAllowed"));
                            } else {
                                final World w = p.getWorld();

                                final AthionMaps pmi = AthionCore.getMap(w);

                                double price = 0;

                                if (AthionCore.isEconomyEnabled(w)) {
                                    price = pmi.AddPlayerPrice;
                                    final double balance = AthionPlots.economy.getBalance(p);

                                    if (balance >= price) {
                                        final EconomyResponse er = AthionPlots.economy.withdrawPlayer(p, price);

                                        if (!er.transactionSuccess()) {
                                            AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                            AthionCommands.warn(er.errorMessage);

                                        }
                                    } else {
                                        AthionCommands.SendMsg(p,
                                        ChatColor.RED + AthionCommands.C("MsgNotEnoughAdd") + " " + AthionCommands.C("WordMissing") + " " + ChatColor.RESET + AthionCommands.f(price - balance, false));

                                    }
                                }

                                plot.addAllowed(allowed);
                                plot.removeDenied(allowed);

                                AthionCommands.SendMsg(p, AthionCommands.C("WordPlayer")
                                + " "
                                + ChatColor.RED
                                + allowed
                                + ChatColor.RESET
                                + " "
                                + AthionCommands.C("MsgNowAllowed")
                                + " "
                                + AthionCommands.f(-price));

                            }
                        } else {
                            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgThisPlot") + "(" + id + ") " + AthionCommands.C("MsgNotYoursNotAllowedAdd"));
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
