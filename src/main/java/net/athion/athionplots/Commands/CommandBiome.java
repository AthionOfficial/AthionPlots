package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionPlot;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

public class CommandBiome {

    public CommandBiome(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "plotme.use.biome")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
            } else {
                final String id = AthionCore.getPlotID(p.getLocation());
                if (id.equals("")) {
                    p.sendMessage(ChatColor.BLUE + AthionCommands.SYSTEM_PREFIX + ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else if (!AthionCore.isPlotAvailable(id, p)) {
                    final World w = p.getWorld();

                    if (args.length == 2) {
                        Biome biome = null;

                        for (final Biome bio : Biome.values()) {
                            if (bio.name().equalsIgnoreCase(args[1])) {
                                biome = bio;
                            }
                        }

                        if (biome == null) {
                            AthionCommands.SendMsg(p, ChatColor.RED + args[1] + ChatColor.RESET + " " + AthionCommands.C("MsgIsInvalidBiome"));
                        } else {
                            final AthionPlot plot = AthionCore.getPlotById(p, id);
                            final String playername = p.getName();

                            if (plot.owner.equalsIgnoreCase(playername) || AthionPlots.cPerms(p, "plotme.admin")) {
                                final AthionMaps pmi = AthionCore.getMap(w);

                                double price = 0;

                                if (AthionCore.isEconomyEnabled(w)) {
                                    price = pmi.BiomeChangePrice;
                                    final double balance = AthionPlots.economy.getBalance(p);

                                    if (balance >= price) {
                                        final EconomyResponse er = AthionPlots.economy.withdrawPlayer(p, price);

                                        if (!er.transactionSuccess()) {
                                            AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                            AthionCommands.warn(er.errorMessage);

                                        }
                                    } else {
                                        AthionCommands.SendMsg(p, ChatColor.RED
                                        + AthionCommands.C("MsgNotEnoughBiome")
                                        + " "
                                        + AthionCommands.C("WordMissing")
                                        + " "
                                        + ChatColor.RESET
                                        + AthionCommands.f(price - balance, false));

                                    }
                                }

                                AthionCore.setBiome(w, id, plot, biome);

                                AthionCommands.SendMsg(p, AthionCommands.C("MsgBiomeSet") + " " + ChatColor.BLUE + AthionCommands.FormatBiome(biome.name()) + " " + AthionCommands.f(-price));

                            } else {
                                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgThisPlot") + "(" + id + ") " + AthionCommands.C("MsgNotYoursNotAllowedBiome"));
                            }
                        }
                    } else {
                        final AthionPlot plot = AthionPlots.AthionMaps.get(w.getName().toLowerCase()).plots.get(id);

                        AthionCommands.SendMsg(p, AthionCommands.C("MsgPlotUsingBiome") + " " + ChatColor.BLUE + AthionCommands.FormatBiome(plot.biome.name()));
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
