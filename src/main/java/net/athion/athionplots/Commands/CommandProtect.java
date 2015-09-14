package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionPlot;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandProtect {

    public CommandProtect(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "plotme.admin.protect") || AthionPlots.cPerms(p, "plotme.use.protect")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));

            } else {
                final String id = AthionCore.getPlotID(p.getLocation());

                if (id.equals("")) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else if (!AthionCore.isPlotAvailable(id, p)) {
                    final AthionPlot plot = AthionCore.getPlotById(p, id);

                    final String name = p.getName();

                    if (plot.owner.equalsIgnoreCase(name) || AthionPlots.cPerms(p, "plotme.admin.protect")) {
                        if (plot.protect) {
                            plot.protect = false;
                            AthionCore.adjustWall(p.getLocation());

                            plot.updateField("protected", false);

                            AthionCommands.SendMsg(p, AthionCommands.C("MsgPlotNoLongerProtected"));
                        } else {
                            final AthionMaps pmi = AthionCore.getMap(p);

                            double cost = 0;

                            if (AthionCore.isEconomyEnabled(p)) {
                                cost = pmi.ProtectPrice;

                                if (AthionPlots.economy.getBalance(p) < cost) {
                                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotEnoughProtectPlot"));

                                } else {
                                    final EconomyResponse er = AthionPlots.economy.withdrawPlayer(p, cost);

                                    if (!er.transactionSuccess()) {
                                        AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                        AthionCommands.warn(er.errorMessage);

                                    }
                                }

                            }

                            plot.protect = true;
                            AthionCore.adjustWall(p.getLocation());

                            plot.updateField("protected", true);

                            AthionCommands.SendMsg(p, AthionCommands.C("MsgPlotNowProtected") + " " + AthionCommands.f(-cost));

                        }
                    } else {
                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgDoNotOwnPlot"));
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
