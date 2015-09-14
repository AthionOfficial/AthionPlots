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

public class CommandUnMerge {

    public CommandUnMerge(final Player p, final String[] args) {
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
                        plots.get((x - 1) + ";" + (z - 1));
                        plots.get((x - 1) + ";" + (z + 1));
                        plots.get((x + 1) + ";" + (z - 1));
                        plots.get((x + 1) + ";" + (z + 1));

                        /*if(p00 != null && p10 != null && p01 != null &&
                        		p00.owner.equalsIgnoreCase(p11.owner) &&
                        		p11.owner.equalsIgnoreCase(p10.owner) &&
                        		p10.owner.equalsIgnoreCase(p01.owner))
                        {
                        	Bukkit.broadcastMessage("Rawr 1");
                        	AthionCore.resetmiddleroad(p00, p11, world);
                        }

                        if(p10 != null && p20 != null && p21 != null &&
                        		p10.owner.equalsIgnoreCase(p11.owner) &&
                        		p11.owner.equalsIgnoreCase(p20.owner) &&
                        		p20.owner.equalsIgnoreCase(p21.owner))
                        {
                        	Bukkit.broadcastMessage("Rawr 2");
                        	AthionCore.resetmiddleroad(p20, p11, world);
                        }

                        if(p01 != null && p02 != null && p12 != null &&
                        		p01.owner.equalsIgnoreCase(p11.owner) &&
                        		p11.owner.equalsIgnoreCase(p02.owner) &&
                        		p02.owner.equalsIgnoreCase(p12.owner))
                        {
                        	Bukkit.broadcastMessage("Rawr 3");
                        	AthionCore.resetmiddleroad(p02, p11, world);
                        }

                        if(p12 != null && p21 != null && p22 != null &&
                        		p12.owner.equalsIgnoreCase(p11.owner) &&
                        		p11.owner.equalsIgnoreCase(p21.owner) &&
                        		p21.owner.equalsIgnoreCase(p22.owner))
                        {
                        	Bukkit.broadcastMessage("Rawr 4");
                        	AthionCore.resetmiddleroad(p22, p11, world);
                        }*/

                        if (p01 != null) {
                            Bukkit.broadcastMessage("Test 1");
                            AthionCore.resetroad(p01, p11, world);
                        }

                        if (p10 != null) {
                            Bukkit.broadcastMessage("Test 2");
                            AthionCore.resetroad(p10, p11, world);
                        }

                        if (p12 != null) {
                            Bukkit.broadcastMessage("Test 3");
                            AthionCore.resetroad(p12, p11, world);
                        }

                        if (p21 != null) {
                            Bukkit.broadcastMessage("Test 4");
                            AthionCore.resetroad(p21, p11, world);
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
