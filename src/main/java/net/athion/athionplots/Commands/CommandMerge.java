package net.athion.athionplots.Commands;

import java.util.HashMap;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionPlot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandMerge {

    public CommandMerge(final Player p, final String[] args) {
        final AthionMaps pmi = AthionCore.getMap(p.getLocation().getWorld());
        final String id = AthionCore.getPlotID(p.getLocation());
        final World world = p.getLocation().getWorld();

        if (AthionPlots.allowToMerge) {
            if (AthionPlots.cPerms(p, "AthionPlots.use.merge")) {
                if (id.equals("")) {

                } else {
                    final HashMap<String, AthionPlot> plots = pmi.plots;

                    final int x = AthionCore.getIdX(id);
                    final int z = AthionCore.getIdZ(id);

                    final AthionPlot p11 = plots.get(id);

                    if (p11 != null) {
                        final AthionPlot p01 = plots.get((x - 1) + ";" + z);
                        final AthionPlot p10 = plots.get(x + ";" + (z - 1));
                        final AthionPlot p12 = plots.get(x + ";" + (z + 1));
                        final AthionPlot p21 = plots.get((x + 1) + ";" + z);
                        final AthionPlot p00 = plots.get((x - 1) + ";" + (z - 1));
                        final AthionPlot p02 = plots.get((x - 1) + ";" + (z + 1));
                        final AthionPlot p20 = plots.get((x + 1) + ";" + (z - 1));
                        final AthionPlot p22 = plots.get((x + 1) + ";" + (z + 1));

                        if ((p01 != null) && p01.owner.equalsIgnoreCase(p11.owner)) {
                            AthionCore.fillroad(p01, p11, world);
                        }

                        if ((p10 != null) && p10.owner.equalsIgnoreCase(p11.owner)) {
                            AthionCore.fillroad(p10, p11, world);
                        }

                        if ((p12 != null) && p12.owner.equalsIgnoreCase(p11.owner)) {
                            AthionCore.fillroad(p12, p11, world);
                        }

                        if ((p21 != null) && p21.owner.equalsIgnoreCase(p11.owner)) {
                            AthionCore.fillroad(p21, p11, world);
                        }

                        if ((p00 != null) && (p10 != null) && (p01 != null) && p00.owner.equalsIgnoreCase(p11.owner) && p11.owner.equalsIgnoreCase(p10.owner) && p10.owner.equalsIgnoreCase(p01.owner)) {
                            AthionCore.fillmiddleroad(p00, p11, world);
                        }

                        if ((p10 != null) && (p20 != null) && (p21 != null) && p10.owner.equalsIgnoreCase(p11.owner) && p11.owner.equalsIgnoreCase(p20.owner) && p20.owner.equalsIgnoreCase(p21.owner)) {
                            AthionCore.fillmiddleroad(p20, p11, world);
                        }

                        if ((p01 != null) && (p02 != null) && (p12 != null) && p01.owner.equalsIgnoreCase(p11.owner) && p11.owner.equalsIgnoreCase(p02.owner) && p02.owner.equalsIgnoreCase(p12.owner)) {
                            AthionCore.fillmiddleroad(p02, p11, world);
                        }

                        if ((p12 != null) && (p21 != null) && (p22 != null) && p12.owner.equalsIgnoreCase(p11.owner) && p11.owner.equalsIgnoreCase(p21.owner) && p21.owner.equalsIgnoreCase(p22.owner)) {
                            AthionCore.fillmiddleroad(p22, p11, world);
                        }

                    } else {
                        Bukkit.broadcastMessage("Failed");
                    }

                }
            } else {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
            }
        }

        /*if (AthionPlots.cPerms(p, "AthionPlots.use.merge")) {

        }*/
    }

}
