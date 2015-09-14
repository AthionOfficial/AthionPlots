package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionPlot;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandBuy {

    public CommandBuy(final Player p, final String[] args) {
        if (AthionCore.isEconomyEnabled(p)) {
            if (AthionPlots.cPerms(p, "AthionPlots.use.buy") || AthionPlots.cPerms(p, "AthionPlots.admin.buy")) {
                final Location l = p.getLocation();
                final String id = AthionCore.getPlotID(l);

                if (id.equals("")) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else if (!AthionCore.isPlotAvailable(id, p)) {
                    final AthionPlot plot = AthionCore.getPlotById(p, id);

                    if (!plot.forsale) {
                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPlotNotForSale"));
                    } else {
                        final String buyer = p.getName();

                        if (plot.owner.equalsIgnoreCase(buyer)) {
                            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgCannotBuyOwnPlot"));
                        } else {
                            final int plotlimit = AthionPlots.getPlotLimit(p);

                            if ((plotlimit != -1) && (AthionCore.getNbOwnedPlot(p) >= plotlimit)) {
                                AthionCommands.SendMsg(p, AthionCommands.C("MsgAlreadyReachedMaxPlots")
                                + " ("
                                + AthionCore.getNbOwnedPlot(p)
                                + "/"
                                + AthionPlots.getPlotLimit(p)
                                + "). "
                                + AthionCommands.C("WordUse")
                                + " "
                                + ChatColor.RED
                                + "/ap "
                                + AthionCommands.C("CommandHome")
                                + ChatColor.RESET
                                + " "
                                + AthionCommands.C("MsgToGetToIt"));
                            } else {
                                final World w = p.getWorld();

                                final double cost = plot.customprice;

                                if (AthionPlots.economy.getBalance(p) < cost) {
                                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotEnoughBuy"));
                                } else {
                                    final EconomyResponse er = AthionPlots.economy.withdrawPlayer(p, cost);

                                    if (er.transactionSuccess()) {
                                        final String oldowner = plot.owner;
                                        final OfflinePlayer playercurrentbidder = Bukkit.getOfflinePlayer(plot.ownerId);

                                        if (!oldowner.equalsIgnoreCase("$Bank$")) {
                                            final EconomyResponse er2 = AthionPlots.economy.depositPlayer(playercurrentbidder, cost);

                                            if (!er2.transactionSuccess()) {
                                                AthionCommands.SendMsg(p, ChatColor.RED + er2.errorMessage);
                                                AthionCommands.warn(er2.errorMessage);
                                            } else {
                                                for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
                                                    if (player.getName().equalsIgnoreCase(oldowner)) {
                                                        AthionCommands.SendMsg(player, AthionCommands.C("WordPlot")
                                                        + " "
                                                        + id
                                                        + " "
                                                        + AthionCommands.C("MsgSoldTo")
                                                        + " "
                                                        + buyer
                                                        + ". "
                                                        + AthionCommands.f(cost));
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        plot.owner = buyer;
                                        plot.customprice = 0;
                                        plot.forsale = false;

                                        plot.updateField("owner", buyer);
                                        plot.updateField("customprice", 0);
                                        plot.updateField("forsale", false);

                                        AthionCore.adjustWall(l);
                                        AthionCore.setSellSign(w, plot);
                                        AthionCore.setOwnerSign(w, plot);

                                        AthionCommands.SendMsg(p, AthionCommands.C("MsgPlotBought") + " " + AthionCommands.f(-cost));
                                    } else {
                                        AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                        AthionCommands.warn(er.errorMessage);
                                    }
                                }
                            }
                        }
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
