package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionPlot;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandSell {

    public CommandSell(final Player p, final String[] args) {
        if (AthionCore.isEconomyEnabled(p)) {
            final AthionMaps pmi = AthionCore.getMap(p);

            if (pmi.CanSellToBank || pmi.CanPutOnSale) {
                if (AthionPlots.cPerms(p, "plotme.use.sell") || AthionPlots.cPerms(p, "plotme.admin.sell")) {
                    final Location l = p.getLocation();
                    final String id = AthionCore.getPlotID(l);

                    if (id.equals("")) {
                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                    } else if (!AthionCore.isPlotAvailable(id, p)) {
                        final AthionPlot plot = AthionCore.getPlotById(p, id);

                        if (plot.owner.equalsIgnoreCase(p.getName()) || AthionPlots.cPerms(p, "plotme.admin.sell")) {
                            final World w = p.getWorld();
                            p.getName();

                            if (plot.forsale) {
                                plot.customprice = 0;
                                plot.forsale = false;

                                plot.updateField("customprice", 0);
                                plot.updateField("forsale", false);

                                AthionCore.adjustWall(l);
                                AthionCore.setSellSign(w, plot);

                                AthionCommands.SendMsg(p, AthionCommands.C("MsgPlotNoLongerSale"));

                            } else {
                                double price = pmi.SellToPlayerPrice;
                                boolean bank = false;

                                if (args.length == 2) {
                                    if (args[1].equalsIgnoreCase("bank")) {
                                        bank = true;
                                    } else if (pmi.CanCustomizeSellPrice) {
                                        try {
                                            price = Double.parseDouble(args[1]);
                                        } catch (final Exception e) {
                                            if (pmi.CanSellToBank) {
                                                AthionCommands.SendMsg(p,
                                                AthionCommands.C("WordUsage") + ": " + ChatColor.RED + "/ap " + AthionCommands.C("CommandSellBank") + "|<" + AthionCommands.C("WordAmount") + ">");
                                                p.sendMessage("  "
                                                + AthionCommands.C("WordExample")
                                                + ": "
                                                + ChatColor.RED
                                                + "/ap "
                                                + AthionCommands.C("CommandSellBank")
                                                + " "
                                                + ChatColor.RESET
                                                + " or "
                                                + ChatColor.RED
                                                + "/ap "
                                                + AthionCommands.C("CommandSell")
                                                + " 200");
                                            } else {
                                                AthionCommands.SendMsg(p,
                                                AthionCommands.C("WordUsage")
                                                + ": "
                                                + ChatColor.RED
                                                + "/ap "
                                                + AthionCommands.C("CommandSell")
                                                + " <"
                                                + AthionCommands.C("WordAmount")
                                                + ">"
                                                + ChatColor.RESET
                                                + " "
                                                + AthionCommands.C("WordExample")
                                                + ": "
                                                + ChatColor.RED
                                                + "/ap "
                                                + AthionCommands.C("CommandSell")
                                                + " 200");
                                            }
                                        }
                                    } else {
                                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgCannotCustomPriceDefault") + " " + price);

                                    }
                                }

                                if (bank) {
                                    if (!pmi.CanSellToBank) {
                                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgCannotSellToBank"));
                                    } else {

                                        final String currentbidder = plot.currentbidder;

                                        if (!currentbidder.equals("")) {
                                            final double bid = plot.currentbid;
                                            final OfflinePlayer playercurrentbidder = Bukkit.getOfflinePlayer(plot.currentbidderId);

                                            final EconomyResponse er = AthionPlots.economy.depositPlayer(playercurrentbidder, bid);

                                            if (!er.transactionSuccess()) {
                                                AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                                AthionCommands.warn(er.errorMessage);
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
                                                        + AthionCommands.C("MsgSoldToBank")
                                                        + " "
                                                        + AthionCommands.f(bid));
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        final double sellprice = pmi.SellToBankPrice;

                                        final EconomyResponse er = AthionPlots.economy.depositPlayer(p, sellprice);

                                        if (er.transactionSuccess()) {
                                            plot.owner = "$Bank$";
                                            plot.forsale = true;
                                            plot.customprice = pmi.BuyFromBankPrice;
                                            plot.auctionned = false;
                                            plot.currentbidder = "";
                                            plot.currentbid = 0;

                                            plot.removeAllAllowed();

                                            AthionCore.setOwnerSign(w, plot);
                                            AthionCore.setSellSign(w, plot);

                                            plot.updateField("owner", plot.owner);
                                            plot.updateField("forsale", true);
                                            plot.updateField("auctionned", true);
                                            plot.updateField("customprice", plot.customprice);
                                            plot.updateField("currentbidder", "");
                                            plot.updateField("currentbid", 0);

                                            AthionCommands.SendMsg(p, AthionCommands.C("MsgPlotSold") + " " + AthionCommands.f(sellprice));
                                        } else {
                                            AthionCommands.SendMsg(p, " " + er.errorMessage);
                                            AthionCommands.warn(er.errorMessage);
                                        }
                                    }
                                } else if (price < 0) {
                                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgInvalidAmount"));
                                } else {
                                    plot.customprice = price;
                                    plot.forsale = true;

                                    plot.updateField("customprice", price);
                                    plot.updateField("forsale", true);

                                    AthionCore.adjustWall(l);
                                    AthionCore.setSellSign(w, plot);

                                    AthionCommands.SendMsg(p, AthionCommands.C("MsgPlotForSale"));
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
