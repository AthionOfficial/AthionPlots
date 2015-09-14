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
import org.bukkit.entity.Player;

public class CommandSetOwner {

    public CommandSetOwner(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "plotme.admin.setowner")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
            } else {
                final String id = AthionCore.getPlotID(p.getLocation());
                if (id.equals("")) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else if ((args.length < 2) || args[1].equals("")) {
                    AthionCommands.SendMsg(p, AthionCommands.C("WordUsage") + ": " + ChatColor.RED + "/ap " + AthionCommands.C("CommandSetowner") + " <" + AthionCommands.C("WordPlayer") + ">");
                } else {
                    final String newowner = args[1];
                    String oldowner = "<" + AthionCommands.C("WordNotApplicable") + ">";
                    p.getName();

                    if (!AthionCore.isPlotAvailable(id, p)) {
                        final AthionPlot plot = AthionCore.getPlotById(p, id);

                        final AthionMaps pmi = AthionCore.getMap(p);
                        oldowner = plot.owner;
                        final OfflinePlayer playeroldowner = Bukkit.getOfflinePlayer(plot.ownerId);

                        if (AthionCore.isEconomyEnabled(p)) {
                            if (pmi.RefundClaimPriceOnSetOwner && (newowner != oldowner)) {
                                final EconomyResponse er = AthionPlots.economy.depositPlayer(playeroldowner, pmi.ClaimPrice);

                                if (!er.transactionSuccess()) {
                                    AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                    AthionCommands.warn(er.errorMessage);

                                } else {
                                    for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
                                        if (player.getName().equalsIgnoreCase(oldowner)) {
                                            AthionCommands.SendMsg(player, AthionCommands.C("MsgYourPlot")
                                            + " "
                                            + id
                                            + " "
                                            + AthionCommands.C("MsgNowOwnedBy")
                                            + " "
                                            + newowner
                                            + ". "
                                            + AthionCommands.f(pmi.ClaimPrice));
                                            break;
                                        }
                                    }
                                }
                            }

                            if ((plot.currentbidder != null) && !plot.currentbidder.equals("")) {
                                final OfflinePlayer playercurrentbidder = Bukkit.getOfflinePlayer(plot.currentbidderId);
                                final EconomyResponse er = AthionPlots.economy.depositPlayer(playercurrentbidder, plot.currentbid);

                                if (!er.transactionSuccess()) {
                                    AthionCommands.SendMsg(p, er.errorMessage);
                                    AthionCommands.warn(er.errorMessage);
                                } else {
                                    for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
                                        if (player.getName().equalsIgnoreCase(plot.currentbidder)) {
                                            AthionCommands.SendMsg(player, AthionCommands.C("WordPlot")
                                            + " "
                                            + id
                                            + " "
                                            + AthionCommands.C("MsgChangedOwnerFrom")
                                            + " "
                                            + oldowner
                                            + " "
                                            + AthionCommands.C("WordTo")
                                            + " "
                                            + newowner
                                            + ". "
                                            + AthionCommands.f(plot.currentbid));
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        plot.currentbidder = "";
                        plot.currentbidderId = null;
                        plot.currentbid = 0;
                        plot.auctionned = false;
                        plot.forsale = false;

                        AthionCore.setSellSign(p.getWorld(), plot);

                        plot.updateField("currentbidder", "");
                        plot.updateField("currentbid", 0);
                        plot.updateField("auctionned", false);
                        plot.updateField("forsale", false);
                        plot.updateField("currentbidderid", null);

                        plot.owner = newowner;

                        AthionCore.setOwnerSign(p.getWorld(), plot);

                        plot.updateField("owner", newowner);
                    } else {
                        AthionCore.claimPlot(p.getWorld(), id, newowner, null);
                    }

                    AthionCommands.SendMsg(p, AthionCommands.C("MsgOwnerChangedTo") + " " + ChatColor.RED + newowner);
                }
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
