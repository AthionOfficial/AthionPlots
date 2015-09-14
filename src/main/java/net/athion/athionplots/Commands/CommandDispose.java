package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionPlot;
import net.athion.athionplots.Core.AthionSQL;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandDispose {

    public CommandDispose(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "AthionPlots.admin.dispose") || AthionPlots.cPerms(p, "AthionPlots.use.dispose")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
            } else {
                final String id = AthionCore.getPlotID(p.getLocation());
                if (id.equals("")) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else if (!AthionCore.isPlotAvailable(id, p)) {
                    final AthionPlot plot = AthionCore.getPlotById(p, id);

                    if (plot.protect) {
                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPlotProtectedNotDisposed"));
                    } else {
                        final String name = p.getName();

                        if (plot.owner.equalsIgnoreCase(name) || AthionPlots.cPerms(p, "AthionPlots.admin.dispose")) {
                            final AthionMaps pmi = AthionCore.getMap(p);

                            final double cost = pmi.DisposePrice;

                            if (AthionCore.isEconomyEnabled(p)) {
                                if ((cost != 0) && (AthionPlots.economy.getBalance(p) < cost)) {
                                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotEnoughDispose"));
                                }

                                final EconomyResponse er = AthionPlots.economy.withdrawPlayer(p, cost);

                                if (!er.transactionSuccess()) {
                                    AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                    AthionCommands.warn(er.errorMessage);
                                }

                                if (plot.auctionned) {
                                    final String currentbidder = plot.currentbidder;
                                    final OfflinePlayer playercurrentbidder = Bukkit.getOfflinePlayer(plot.currentbidderId);

                                    if (!currentbidder.equals("")) {
                                        final EconomyResponse er2 = AthionPlots.economy.depositPlayer(playercurrentbidder, plot.currentbid);

                                        if (!er2.transactionSuccess()) {
                                            AthionCommands.SendMsg(p, ChatColor.RED + er2.errorMessage);
                                            AthionCommands.warn(er2.errorMessage);
                                        } else {
                                            for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
                                                if (player.getName().equalsIgnoreCase(currentbidder)) {
                                                    AthionCommands.SendMsg(player, AthionCommands.C("WordPlot")
                                                    + " "
                                                    + id
                                                    + " "
                                                    + AthionCommands.C("MsgOwnedBy")
                                                    + " "
                                                    + plot.owner
                                                    + " "
                                                    + AthionCommands.C("MsgWasDisposed")
                                                    + " "
                                                    + AthionCommands.f(cost));
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            final World w = p.getWorld();

                            if (!AthionCore.isPlotAvailable(id, p)) {
                                AthionCore.getPlots(p).remove(id);
                            }

                            AthionCore.removeOwnerSign(w, id);
                            AthionCore.removeSellSign(w, id);

                            AthionSQL.deletePlot(AthionCore.getIdX(id), AthionCore.getIdZ(id), w.getName().toLowerCase());

                            AthionCommands.SendMsg(p, AthionCommands.C("MsgPlotDisposedAnyoneClaim"));

                        } else {
                            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgThisPlot") + "(" + id + ") " + AthionCommands.C("MsgNotYoursCannotDispose"));
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
