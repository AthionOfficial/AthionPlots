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
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

public class CommandReset {

    public CommandReset(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "plotme.admin.reset")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
            } else {
                final AthionPlot plot = AthionCore.getPlotById(p.getLocation());

                if (plot == null) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else if (plot.protect) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPlotProtectedCannotReset"));
                } else {
                    final String id = plot.id;
                    final World w = p.getWorld();

                    AthionCore.setBiome(w, id, plot, Biome.PLAINS);
                    AthionCore.clear(w, plot);
                    //RemoveLWC(w, plot);

                    if (AthionCore.isEconomyEnabled(p)) {
                        if (plot.auctionned) {
                            final String currentbidder = plot.currentbidder;

                            if (!currentbidder.equals("")) {
                                final OfflinePlayer playercurrentbidder = Bukkit.getOfflinePlayer(plot.currentbidderId);
                                final EconomyResponse er = AthionPlots.economy.depositPlayer(playercurrentbidder, plot.currentbid);

                                if (!er.transactionSuccess()) {
                                    AthionCommands.SendMsg(p, er.errorMessage);
                                    AthionCommands.warn(er.errorMessage);
                                } else {
                                    for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
                                        if (player.getName().equalsIgnoreCase(currentbidder)) {
                                            AthionCommands.SendMsg(player,
                                            AthionCommands.C("WordPlot")
                                            + " "
                                            + id
                                            + " "
                                            + AthionCommands.C("MsgOwnedBy")
                                            + " "
                                            + plot.owner
                                            + " "
                                            + AthionCommands.C("MsgWasReset")
                                            + " "
                                            + AthionCommands.f(plot.currentbid));
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        final AthionMaps pmi = AthionCore.getMap(p);

                        if (pmi.RefundClaimPriceOnReset) {
                            final OfflinePlayer playerowner = Bukkit.getOfflinePlayer(plot.ownerId);
                            final EconomyResponse er = AthionPlots.economy.depositPlayer(playerowner, pmi.ClaimPrice);

                            if (!er.transactionSuccess()) {
                                AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                AthionCommands.warn(er.errorMessage);

                            } else {
                                for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
                                    if (player.getName().equalsIgnoreCase(plot.owner)) {
                                        AthionCommands.SendMsg(player,
                                        AthionCommands.C("WordPlot")
                                        + " "
                                        + id
                                        + " "
                                        + AthionCommands.C("MsgOwnedBy")
                                        + " "
                                        + plot.owner
                                        + " "
                                        + AthionCommands.C("MsgWasReset")
                                        + " "
                                        + AthionCommands.f(pmi.ClaimPrice));
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (!AthionCore.isPlotAvailable(id, p)) {
                        AthionCore.getPlots(p).remove(id);
                    }

                    p.getName();

                    AthionCore.removeOwnerSign(w, id);
                    AthionCore.removeSellSign(w, id);

                    AthionSQL.deletePlot(AthionCore.getIdX(id), AthionCore.getIdZ(id), w.getName().toLowerCase());

                    AthionCommands.SendMsg(p, AthionCommands.C("MsgPlotReset"));

                }
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
