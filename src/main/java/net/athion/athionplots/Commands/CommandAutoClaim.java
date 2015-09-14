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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandAutoClaim {

    public CommandAutoClaim(final Player p, final String[] args) {
        auto(p, args);
    }

    private boolean auto(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "plotme.use.auto")) {
            if (!AthionCore.isPlotWorld(p)) {
                if (!AthionPlots.allowWorldTeleport) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
                } else {
                    World w;

                    if (!AthionCore.isPlotWorld(p)) {
                        if (args.length == 2) {
                            w = Bukkit.getWorld(args[1]);
                        } else {
                            w = AthionCore.getFirstWorld();
                        }

                        if ((w == null) || !AthionCore.isPlotWorld(w)) {
                            AthionCommands.SendMsg(p, ChatColor.RED + args[1] + " " + AthionCommands.C("MsgWorldNotPlot"));
                            return true;
                        }
                    } else {
                        w = p.getWorld();
                    }

                    if (w == null) {
                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotworldFound"));
                    } else if ((AthionCore.getNbOwnedPlot(p, w) >= AthionPlots.getPlotLimit(p)) && !AthionPlots.cPerms(p, "plotme.admin")) {
                        AthionCommands.SendMsg(p, ChatColor.RED
                        + AthionCommands.C("MsgAlreadyReachedMaxPlots")
                        + " ("
                        + AthionCore.getNbOwnedPlot(p, w)
                        + "/"
                        + AthionPlots.getPlotLimit(p)
                        + "). "
                        + AthionCommands.C("WordUse")
                        + " "
                        + "/ap "
                        + AthionCommands.C("CommandHome")
                        + " "
                        + AthionCommands.C("MsgToGetToIt"));
                    } else {
                        final AthionMaps pmi = AthionCore.getMap(w);
                        final int limit = pmi.PlotAutoLimit;

                        for (int i = 0; i < limit; i++) {
                            for (int x = -i; x <= i; x++) {
                                for (int z = -i; z <= i; z++) {
                                    final String id = "" + x + ";" + z;

                                    if (AthionCore.isPlotAvailable(id, w)) {
                                        final String name = p.getName();
                                        final UUID uuid = p.getUniqueId();

                                        double price = 0;

                                        if (AthionCore.isEconomyEnabled(w)) {
                                            price = pmi.ClaimPrice;
                                            final double balance = AthionPlots.economy.getBalance(p);

                                            if (balance >= price) {
                                                final EconomyResponse er = AthionPlots.economy.withdrawPlayer(p, price);

                                                if (!er.transactionSuccess()) {
                                                    AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                                    AthionCommands.warn(er.errorMessage);
                                                    return true;
                                                }
                                            } else {
                                                AthionCommands.SendMsg(p, ChatColor.RED
                                                + AthionCommands.C("MsgNotEnoughAuto")
                                                + " "
                                                + AthionCommands.C("WordMissing")
                                                + " "
                                                + ChatColor.RESET
                                                + AthionCommands.f(price - balance, false));
                                                return true;
                                            }
                                        }

                                        final AthionPlot plot = AthionCore.claimPlot(w, id, name, uuid);

                                        //AthionManager.adjustLinkedPlots(id, w);

                                        p.teleport(new Location(w, AthionCore.bottomX(plot.id, w) + ((AthionCore.topX(plot.id, w) - AthionCore.bottomX(plot.id, w)) / 2), pmi.RoadHeight + 2,
                                        AthionCore.bottomZ(plot.id, w) - 2));

                                        AthionCommands.SendMsg(p,
                                        AthionCommands.C("MsgThisPlotYours")
                                        + " "
                                        + AthionCommands.C("WordUse")
                                        + " "
                                        + ChatColor.RED
                                        + "/ap "
                                        + AthionCommands.C("CommandHome")
                                        + ChatColor.RESET
                                        + " "
                                        + AthionCommands.C("MsgToGetToIt")
                                        + " "
                                        + AthionCommands.f(-price));

                                        return true;
                                    }
                                }
                            }
                        }

                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound1") + " " + (limit ^ 2) + " " + AthionCommands.C("MsgNoPlotFound2"));
                    }
                }
            } else {
                World w;
                if (!AthionCore.isPlotWorld(p)) {
                    if (AthionPlots.allowWorldTeleport) {
                        if (args.length == 2) {
                            w = Bukkit.getWorld(args[1]);
                        } else {
                            w = AthionCore.getFirstWorld();
                        }

                        if ((w == null) || !AthionCore.isPlotWorld(w)) {
                            AthionCommands.SendMsg(p, ChatColor.RED + args[1] + " " + AthionCommands.C("MsgWorldNotPlot"));
                            return true;
                        }
                    } else {
                        w = p.getWorld();
                    }
                } else {
                    w = p.getWorld();
                }

                if (w == null) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotworldFound"));
                } else if ((AthionCore.getNbOwnedPlot(p, w) >= AthionPlots.getPlotLimit(p)) && !AthionPlots.cPerms(p, "plotme.admin")) {
                    AthionCommands.SendMsg(p, ChatColor.RED
                    + AthionCommands.C("MsgAlreadyReachedMaxPlots")
                    + " ("
                    + AthionCore.getNbOwnedPlot(p, w)
                    + "/"
                    + AthionPlots.getPlotLimit(p)
                    + "). "
                    + AthionCommands.C("WordUse")
                    + " "
                    + "/ap "
                    + AthionCommands.C("CommandHome")
                    + " "
                    + AthionCommands.C("MsgToGetToIt"));
                } else {
                    final AthionMaps pmi = AthionCore.getMap(w);
                    final int limit = pmi.PlotAutoLimit;

                    for (int i = 0; i < limit; i++) {
                        for (int x = -i; x <= i; x++) {
                            for (int z = -i; z <= i; z++) {
                                final String id = "" + x + ";" + z;

                                if (AthionCore.isPlotAvailable(id, w)) {
                                    final String name = p.getName();
                                    final UUID uuid = p.getUniqueId();

                                    double price = 0;

                                    if (AthionCore.isEconomyEnabled(w)) {
                                        price = pmi.ClaimPrice;
                                        final double balance = AthionPlots.economy.getBalance(p);

                                        if (balance >= price) {
                                            final EconomyResponse er = AthionPlots.economy.withdrawPlayer(p, price);

                                            if (!er.transactionSuccess()) {
                                                AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                                AthionCommands.warn(er.errorMessage);
                                                return true;
                                            }
                                        } else {
                                            AthionCommands.SendMsg(p, ChatColor.RED
                                            + AthionCommands.C("MsgNotEnoughAuto")
                                            + " "
                                            + AthionCommands.C("WordMissing")
                                            + " "
                                            + ChatColor.RESET
                                            + AthionCommands.f(price - balance, false));
                                            return true;
                                        }
                                    }

                                    final AthionPlot plot = AthionCore.claimPlot(w, id, name, uuid);

                                    //AthionManager.adjustLinkedPlots(id, w);

                                    p.teleport(new Location(w, AthionCore.bottomX(plot.id, w) + ((AthionCore.topX(plot.id, w) - AthionCore.bottomX(plot.id, w)) / 2), pmi.RoadHeight + 2, AthionCore
                                    .bottomZ(plot.id, w) - 2));

                                    AthionCommands.SendMsg(p, AthionCommands.C("MsgThisPlotYours")
                                    + " "
                                    + AthionCommands.C("WordUse")
                                    + " "
                                    + ChatColor.RED
                                    + "/ap "
                                    + AthionCommands.C("CommandHome")
                                    + ChatColor.RESET
                                    + " "
                                    + AthionCommands.C("MsgToGetToIt")
                                    + " "
                                    + AthionCommands.f(-price));

                                    return true;
                                }
                            }
                        }
                    }

                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound1") + " " + (limit ^ 2) + " " + AthionCommands.C("MsgNoPlotFound2"));
                }
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }
        return true;
    }

}
