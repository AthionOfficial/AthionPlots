package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionPlot;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandAuction {

    public CommandAuction(final Player p, final String[] args) {
        if (AthionCore.isEconomyEnabled(p)) {
            final AthionMaps pmi = AthionCore.getMap(p);

            if (pmi.CanPutOnSale) {
                if (AthionPlots.cPerms(p, "AthionPlots.use.auction") || AthionPlots.cPerms(p, "AthionPlots.admin.auction")) {
                    final String id = AthionCore.getPlotID(p.getLocation());

                    if (id.equals("")) {
                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                    } else if (!AthionCore.isPlotAvailable(id, p)) {
                        final AthionPlot plot = AthionCore.getPlotById(p, id);

                        final String name = p.getName();

                        if (plot.owner.equalsIgnoreCase(name) || AthionPlots.cPerms(p, "AthionPlots.admin.auction")) {
                            final World w = p.getWorld();

                            if (plot.auctionned) {
                                if (plot.currentbidder != null) {
                                    if (!plot.currentbidder.equalsIgnoreCase("")) {
                                        if (!AthionPlots.cPerms(p, "AthionPlots.admin.auction")) {
                                            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPlotHasBidsAskAdmin"));
                                        } else {
                                            if (plot.currentbidder != null) {
                                                final OfflinePlayer playercurrentbidder = Bukkit.getOfflinePlayer(plot.currentbidderId);
                                                final EconomyResponse er = AthionPlots.economy.depositPlayer(playercurrentbidder, plot.currentbid);

                                                if (!er.transactionSuccess()) {
                                                    AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                                    AthionCommands.warn(er.errorMessage);
                                                } else {
                                                    for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
                                                        if ((plot.currentbidder != null) && player.getName().equalsIgnoreCase(plot.currentbidder)) {
                                                            AthionCommands.SendMsg(player, AthionCommands.C("MsgAuctionCancelledOnPlot")
                                                            + " "
                                                            + id
                                                            + " "
                                                            + AthionCommands.C("MsgOwnedBy")
                                                            + " "
                                                            + plot.owner
                                                            + ". "
                                                            + AthionCommands.f(plot.currentbid));
                                                            break;
                                                        }
                                                    }
                                                }
                                            }

                                            plot.auctionned = false;
                                            AthionCore.adjustWall(p.getLocation());
                                            AthionCore.setSellSign(w, plot);
                                            plot.currentbid = 0;
                                            plot.currentbidder = "";

                                            plot.updateField("currentbid", 0);
                                            plot.updateField("currentbidder", "");
                                            plot.updateField("auctionned", false);

                                            AthionCommands.SendMsg(p, AthionCommands.C("MsgAuctionCancelled"));
                                        }
                                    } else {
                                        plot.auctionned = false;
                                        AthionCore.adjustWall(p.getLocation());
                                        AthionCore.setSellSign(w, plot);
                                        plot.currentbid = 0;
                                        plot.currentbidder = "";

                                        plot.updateField("currentbid", 0);
                                        plot.updateField("currentbidder", "");
                                        plot.updateField("auctionned", false);

                                        AthionCommands.SendMsg(p, AthionCommands.C("MsgAuctionCancelled"));
                                    }
                                } else {
                                    plot.auctionned = false;
                                    AthionCore.adjustWall(p.getLocation());
                                    AthionCore.setSellSign(w, plot);
                                    plot.currentbid = 0;
                                    plot.currentbidder = "";

                                    plot.updateField("currentbid", 0);
                                    plot.updateField("currentbidder", "");
                                    plot.updateField("auctionned", false);

                                    AthionCommands.SendMsg(p, AthionCommands.C("MsgAuctionCancelled"));
                                }
                            } else {
                                double bid = 1;

                                if (args.length == 2) {
                                    try {
                                        bid = Double.parseDouble(args[1]);
                                    } catch (final NumberFormatException ignored) {}
                                }

                                if (bid < 0) {
                                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgInvalidAmount"));
                                } else {
                                    plot.currentbid = bid;
                                    plot.auctionned = true;
                                    AthionCore.adjustWall(p.getLocation());
                                    AthionCore.setSellSign(w, plot);

                                    plot.updateField("currentbid", bid);
                                    plot.updateField("auctionned", true);

                                    AthionCommands.SendMsg(p, AthionCommands.C("MsgAuctionStarted"));
                                }
                            }
                        } else {
                            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgDoNotOwnPlot"));
                        }
                    } else {
                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgThisPlot") + "(" + id + ") " + AthionCommands.C("MsgHasNoOwner"));
                    }
                } else {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
                }
            } else {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgSellingPlotsIsDisabledWorld"));
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgEconomyDisabledWorld"));
        }

    }

}
