package net.athion.athionplots.Commands;

import java.util.UUID;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionPlot;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandHome {

    public CommandHome(final Player p, final String[] args) {
        home(p, args);
    }

    private boolean home(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "plotme.use.home") || AthionPlots.cPerms(p, "plotme.admin.home.other")) {
            if (!AthionCore.isPlotWorld(p)) {
                if (!AthionPlots.allowWorldTeleport) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
                } else {
                    String playername = p.getName();
                    UUID uuid = p.getUniqueId();
                    int nb = 1;
                    World w;
                    String worldname = "";

                    if (!AthionCore.isPlotWorld(p)) {
                        w = AthionCore.getFirstWorld();
                    } else {
                        w = p.getWorld();
                    }

                    if (w != null) {
                        worldname = w.getName();
                    }

                    if (args[0].contains(":")) {
                        Bukkit.broadcastMessage("NB: " + nb);
                        try {
                            if ((args[0].split(":").length == 1) || args[0].split(":")[1].equals("")) {
                                AthionCommands.SendMsg(p, AthionCommands.C("WordUsage")
                                + ": "
                                + ChatColor.RED
                                + "/AthionPlots. "
                                + AthionCommands.C("CommandHome")
                                + ":# "
                                + ChatColor.RESET
                                + AthionCommands.C("WordExample")
                                + ": "
                                + ChatColor.RED
                                + "/AthionPlots. "
                                + AthionCommands.C("CommandHome")
                                + ":1");
                                return true;
                            } else {
                                nb = Integer.parseInt(args[0].split(":")[1]);
                            }
                        } catch (final NumberFormatException ex) {
                            AthionCommands.SendMsg(p, AthionCommands.C("WordUsage")
                            + ": "
                            + ChatColor.RED
                            + "/AthionPlots. "
                            + AthionCommands.C("CommandHome")
                            + ":# "
                            + ChatColor.RESET
                            + AthionCommands.C("WordExample")
                            + ": "
                            + ChatColor.RED
                            + "/AthionPlots. "
                            + AthionCommands.C("CommandHome")
                            + ":1");
                            return true;
                        }
                    }

                    if (args.length == 2) {
                        if (Bukkit.getWorld(args[1]) == null) {
                            if (AthionPlots.cPerms(p, "plotme.admin.home.other")) {
                                playername = args[1];
                                uuid = null;
                            }
                        } else {
                            w = Bukkit.getWorld(args[1]);
                            worldname = args[1];
                        }
                    }

                    if (args.length == 3) {
                        if (Bukkit.getWorld(args[2]) == null) {
                            AthionCommands.SendMsg(p, ChatColor.RED + args[2] + " " + AthionCommands.C("MsgWorldNotPlot"));
                            return true;
                        } else {
                            w = Bukkit.getWorld(args[2]);
                            worldname = args[2];
                        }
                    }

                    if (!AthionCore.isPlotWorld(w)) {
                        AthionCommands.SendMsg(p, ChatColor.RED + worldname + " " + AthionCommands.C("MsgWorldNotPlot"));
                    } else {
                        int i = nb - 1;

                        for (final AthionPlot plot : AthionCore.getPlots(w).values()) {
                            if (((uuid == null) && plot.owner.equalsIgnoreCase(playername)) || ((uuid != null) && (plot.ownerId != null) && plot.ownerId.equals(uuid))) {
                                if (i == 0) {
                                    final AthionMaps pmi = AthionCore.getMap(w);

                                    double price = 0;

                                    if (AthionCore.isEconomyEnabled(w)) {
                                        price = pmi.PlotHomePrice;
                                        final double balance = AthionPlots.economy.getBalance(p);

                                        if (balance >= price) {
                                            final EconomyResponse er = AthionPlots.economy.withdrawPlayer(p, price);

                                            if (!er.transactionSuccess()) {
                                                AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                                return true;
                                            }
                                        } else {
                                            AthionCommands.SendMsg(p, ChatColor.RED
                                            + AthionCommands.C("MsgNotEnoughTp")
                                            + " "
                                            + AthionCommands.C("WordMissing")
                                            + " "
                                            + ChatColor.RESET
                                            + AthionCommands.f(price - balance, false));
                                            return true;
                                        }
                                    }

                                    p.teleport(AthionCore.getPlotHome(w, plot));

                                    if (price != 0) {
                                        AthionCommands.SendMsg(p, AthionCommands.f(-price));
                                    }

                                    return true;
                                } else {
                                    i--;
                                }
                            }
                        }

                        if (nb > 0) {
                            if (!playername.equalsIgnoreCase(p.getName())) {
                                AthionCommands.SendMsg(p, ChatColor.RED + playername + " " + AthionCommands.C("MsgDoesNotHavePlot") + " #" + nb);
                            } else {
                                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPlotNotFound") + " #" + nb);
                            }
                        } else if (!playername.equalsIgnoreCase(p.getName())) {
                            AthionCommands.SendMsg(p, ChatColor.RED + playername + " " + AthionCommands.C("MsgDoesNotHavePlot"));
                        } else {
                            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgYouHaveNoPlot"));
                        }
                    }
                }
            } else {
                String playername = p.getName();
                UUID uuid = p.getUniqueId();
                int nb = 1;
                World w;
                String worldname = "";

                if (!AthionCore.isPlotWorld(p) && AthionPlots.allowWorldTeleport) {
                    w = AthionCore.getFirstWorld();
                } else {
                    w = p.getWorld();
                }

                if (w != null) {
                    worldname = w.getName();
                }

                if (args[0].contains(":")) {
                    try {
                        if ((args[0].split(":").length == 1) || args[0].split(":")[1].equals("")) {
                            AthionCommands.SendMsg(p,
                            AthionCommands.C("WordUsage")
                            + ": "
                            + ChatColor.RED
                            + "/ap "
                            + AthionCommands.C("CommandHome")
                            + ":# "
                            + ChatColor.RESET
                            + AthionCommands.C("WordExample")
                            + ": "
                            + ChatColor.RED
                            + "/ap "
                            + AthionCommands.C("CommandHome")
                            + ":1");
                            return true;
                        } else {
                            nb = Integer.parseInt(args[0].split(":")[1]);
                        }
                    } catch (final Exception ex) {
                        AthionCommands.SendMsg(p,
                        AthionCommands.C("WordUsage")
                        + ": "
                        + ChatColor.RED
                        + "/ap "
                        + AthionCommands.C("CommandHome")
                        + ":# "
                        + ChatColor.RESET
                        + AthionCommands.C("WordExample")
                        + ": "
                        + ChatColor.RED
                        + "/ap "
                        + AthionCommands.C("CommandHome")
                        + ":1");
                        return true;
                    }
                }

                if (args.length == 2) {
                    if (Bukkit.getWorld(args[1]) == null) {
                        if (AthionPlots.cPerms(p, "plotme.admin.home.other")) {
                            playername = args[1];
                            uuid = null;
                        }
                    } else {
                        w = Bukkit.getWorld(args[1]);
                        worldname = args[1];
                    }
                }

                if (args.length == 3) {
                    if (Bukkit.getWorld(args[2]) == null) {
                        AthionCommands.SendMsg(p, ChatColor.RED + args[2] + " " + AthionCommands.C("MsgWorldNotPlot"));
                        return true;
                    } else {
                        w = Bukkit.getWorld(args[2]);
                        worldname = args[2];
                    }
                }

                if (!AthionCore.isPlotWorld(w)) {
                    AthionCommands.SendMsg(p, ChatColor.RED + worldname + " " + AthionCommands.C("MsgWorldNotPlot"));
                } else {
                    int i = nb - 1;

                    for (final AthionPlot plot : AthionCore.getPlots(w).values()) {
                        if (((uuid == null) && plot.owner.equalsIgnoreCase(playername)) || ((uuid != null) && (plot.ownerId != null) && plot.ownerId.equals(uuid))) {
                            if (i == 0) {
                                final AthionMaps pmi = AthionCore.getMap(w);

                                double price = 0;

                                if (AthionCore.isEconomyEnabled(w)) {
                                    price = pmi.PlotHomePrice;
                                    final double balance = AthionPlots.economy.getBalance(p);

                                    if (balance >= price) {
                                        final EconomyResponse er = AthionPlots.economy.withdrawPlayer(p, price);

                                        if (!er.transactionSuccess()) {
                                            AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                            return true;
                                        }
                                    } else {
                                        AthionCommands.SendMsg(p,
                                        ChatColor.RED + AthionCommands.C("MsgNotEnoughTp") + " " + AthionCommands.C("WordMissing") + " " + ChatColor.RESET + AthionCommands.f(price - balance, false));
                                        return true;
                                    }
                                }

                                p.teleport(AthionCore.getPlotHome(w, plot));

                                if (price != 0) {
                                    AthionCommands.SendMsg(p, AthionCommands.f(-price));
                                }

                                return true;
                            } else {
                                i--;
                            }
                        }
                    }

                    if (nb > 0) {
                        if (!playername.equalsIgnoreCase(p.getName())) {
                            AthionCommands.SendMsg(p, ChatColor.RED + playername + " " + AthionCommands.C("MsgDoesNotHavePlot") + " #" + nb);
                        } else {
                            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPlotNotFound") + " #" + nb);
                        }
                    } else if (!playername.equalsIgnoreCase(p.getName())) {
                        AthionCommands.SendMsg(p, ChatColor.RED + playername + " " + AthionCommands.C("MsgDoesNotHavePlot"));
                    } else {
                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgYouHaveNoPlot"));
                    }
                }
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }
        return true;
    }

}
