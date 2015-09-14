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

public class CommandClear {

    public CommandClear(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "plotme.admin.clear") || AthionPlots.cPerms(p, "plotme.use.clear")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
            } else {
                final String id = AthionCore.getPlotID(p.getLocation());
                if (id.equals("")) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else if (!AthionCore.isPlotAvailable(id, p)) {
                    final AthionPlot plot = AthionCore.getPlotById(p, id);

                    if (plot.protect) {
                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPlotProtectedCannotClear"));
                    } else {
                        final String playername = p.getName();

                        if (plot.owner.equalsIgnoreCase(playername) || AthionPlots.cPerms(p, "plotme.admin.clear")) {
                            final World w = p.getWorld();

                            final AthionMaps pmi = AthionCore.getMap(w);

                            double price = 0;

                            if (AthionCore.isEconomyEnabled(w)) {
                                price = pmi.ClearPrice;
                                final double balance = AthionPlots.economy.getBalance(p);

                                if (balance >= price) {
                                    final EconomyResponse er = AthionPlots.economy.withdrawPlayer(p, price);

                                    if (!er.transactionSuccess()) {
                                        AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                        AthionCommands.warn(er.errorMessage);
                                    }
                                } else {
                                    AthionCommands.SendMsg(p, ChatColor.RED
                                    + AthionCommands.C("MsgNotEnoughClear")
                                    + " "
                                    + AthionCommands.C("WordMissing")
                                    + " "
                                    + ChatColor.RESET
                                    + (price - balance)
                                    + ChatColor.RED
                                    + " "
                                    + AthionPlots.economy.currencyNamePlural());
                                }
                            }

                            AthionCore.clear(w, plot);
                            //RemoveLWC(w, plot, p);
                            //AthionManager.regen(w, plot);

                            AthionCommands.SendMsg(p, AthionCommands.C("MsgPlotCleared") + ChatColor.RED + " " + AthionCommands.f(-price));

                        } else {
                            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgThisPlot") + "(" + id + ") " + AthionCommands.C("MsgNotYoursNotAllowedClear"));
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
