package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionPlot;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CommandBid {

    public CommandBid(final Player p, final String[] args) {
        if (AthionCore.isEconomyEnabled(p)) {
            if (AthionPlots.cPerms(p, "plotme.use.bid")) {
                final String id = AthionCore.getPlotID(p.getLocation());

                if (id.equals("")) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else if (!AthionCore.isPlotAvailable(id, p)) {
                    final AthionPlot plot = AthionCore.getPlotById(p, id);

                    if (plot.auctionned) {
                        final String bidder = p.getName();
                        final OfflinePlayer playerbidder = p;

                        if (plot.owner.equalsIgnoreCase(bidder)) {
                            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgCannotBidOwnPlot"));
                        } else if (args.length == 2) {
                            double bid = 0;
                            final double currentbid = plot.currentbid;
                            final String currentbidder = plot.currentbidder;
                            final OfflinePlayer playercurrentbidder = Bukkit.getOfflinePlayer(plot.currentbidderId);

                            try {
                                bid = Double.parseDouble(args[1]);
                            } catch (final NumberFormatException ignored) {}

                            final boolean equals = currentbidder.equals("");
                            if (bid < currentbid) {
                                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgInvalidBidMustBeAbove") + " " + ChatColor.RESET + AthionCommands.f(plot.currentbid, false));
                            } else if (bid == currentbid) {
                                if (!equals) {
                                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgInvalidBidMustBeAbove") + " " + ChatColor.RESET + AthionCommands.f(plot.currentbid, false));
                                } else {
                                    final double balance = AthionPlots.economy.getBalance(playerbidder);

                                    if (((bid >= balance) && !currentbidder.equals(bidder)) || (currentbidder.equals(bidder) && (bid > (balance + currentbid)))) {
                                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotEnoughBid"));
                                    } else {
                                        final EconomyResponse er = AthionPlots.economy.withdrawPlayer(playerbidder, bid);

                                        if (er.transactionSuccess()) {

                                            plot.currentbidder = bidder;
                                            plot.currentbid = bid;

                                            plot.updateField("currentbidder", bidder);
                                            plot.updateField("currentbid", bid);

                                            AthionCore.setSellSign(p.getWorld(), plot);

                                            AthionCommands.SendMsg(p, AthionCommands.C("MsgBidAccepted") + " " + AthionCommands.f(-bid));
                                        } else {
                                            AthionCommands.SendMsg(p, er.errorMessage);
                                            AthionCommands.warn(er.errorMessage);
                                        }
                                    }
                                }
                            } else {
                                final double balance = AthionPlots.economy.getBalance(playerbidder);

                                if (((bid >= balance) && !currentbidder.equals(bidder)) || (currentbidder.equals(bidder) && (bid > (balance + currentbid)))) {
                                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotEnoughBid"));
                                } else {
                                    final EconomyResponse er = AthionPlots.economy.withdrawPlayer(playerbidder, bid);

                                    if (er.transactionSuccess()) {
                                        if (!equals) {
                                            final EconomyResponse er2 = AthionPlots.economy.depositPlayer(playercurrentbidder, currentbid);

                                            if (!er2.transactionSuccess()) {
                                                AthionCommands.SendMsg(p, er2.errorMessage);
                                                AthionCommands.warn(er2.errorMessage);
                                            } else {
                                                for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
                                                    if (player.getName().equalsIgnoreCase(currentbidder)) {
                                                        AthionCommands.SendMsg(player, AthionCommands.C("MsgOutbidOnPlot")
                                                        + " "
                                                        + id
                                                        + " "
                                                        + AthionCommands.C("MsgOwnedBy")
                                                        + " "
                                                        + plot.owner
                                                        + ". "
                                                        + AthionCommands.f(bid));
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        plot.currentbidder = bidder;
                                        plot.currentbid = bid;

                                        plot.updateField("currentbidder", bidder);
                                        plot.updateField("currentbid", bid);

                                        AthionCore.setSellSign(p.getWorld(), plot);

                                        AthionCommands.SendMsg(p, AthionCommands.C("MsgBidAccepted") + " " + AthionCommands.f(-bid));

                                    } else {
                                        AthionCommands.SendMsg(p, er.errorMessage);
                                        AthionCommands.warn(er.errorMessage);
                                    }
                                }
                            }
                        } else {
                            AthionCommands.SendMsg(p, AthionCommands.C("WordUsage")
                            + ": "
                            + ChatColor.RED
                            + "/ap "
                            + AthionCommands.C("CommandBid")
                            + " <"
                            + AthionCommands.C("WordAmount")
                            + "> "
                            + ChatColor.RESET
                            + AthionCommands.C("WordExample")
                            + ": "
                            + ChatColor.RED
                            + "/ap "
                            + AthionCommands.C("CommandBid")
                            + " 100");
                        }
                    } else {
                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPlotNotAuctionned"));
                    }
                } else {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgThisPlot") + "(" + id + ") " + AthionCommands.C("MsgHasNoOwner"));
                }
            } else {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgEconomyDisabledWorld"));
        }

    }

}
