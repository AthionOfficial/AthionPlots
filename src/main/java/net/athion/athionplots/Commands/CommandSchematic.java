package net.athion.athionplots.Commands;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionPlot;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;

public class CommandSchematic {

    public CommandSchematic(final CommandSender cs, final String[] args, final Plugin plugin) {
        final Player p = (Player) cs;
        final WorldEditPlugin we = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
        final LocalSession localSession = we.getSession(p);
        //        com.sk89q.worldedit.LocalPlayer wePlayer = we.getSwrapCommandSender(cs);

        final AthionMaps pmi = AthionCore.getMap(p.getLocation().getWorld());
        final String id = AthionCore.getPlotID(p.getLocation());
        final World world = p.getLocation().getWorld();

        if (AthionPlots.allowToSchematic) {
            if (AthionPlots.cPerms(p, "plotme.use.schematic")) {
                if (id.equals("")) {

                } else {
                    AthionPlots.removeIgnoreWELimit(p);
                    final HashMap<String, AthionPlot> plots = pmi.plots;

                    final int size = pmi.PlotSize + pmi.PathWidth;

                    final int valx = p.getLocation().getBlockX();
                    final int valz = p.getLocation().getBlockZ();

                    final int x = (int) Math.ceil((double) valx / size);
                    final int z = (int) Math.ceil((double) valz / size);

                    int xN = 0;
                    int zN = 0;

                    if (x > 0) {
                        xN = (int) Math.ceil(((double) x * size) - 4 - pmi.PlotSize);
                    } else if (x <= 0) {
                        xN = (int) Math.ceil(((double) x * size) - pmi.PlotSize - 4);
                    }

                    if (z > 0) {
                        zN = (int) Math.ceil(((double) z * size) - pmi.PlotSize - 5);
                    } else if (z <= 0) {
                        zN = (int) Math.ceil(((double) z * size) - pmi.PlotSize - 5);
                    }

                    final int y = pmi.RoadHeight + 1;

                    final Vector vector = new Vector(xN, y, zN);
                    final AthionPlot a1 = plots.get(id);

                    /*p.sendMessage("X:" + xN);
                    p.sendMessage("Z:" + zN);*/

                    final String id2 = AthionCore.getPlotID(p.getLocation());
                    final AthionPlot plot = AthionCore.getPlotById(p.getLocation().getWorld(), id2);

                    if (args.length == 3) {

                        if (args[1].equalsIgnoreCase("load")) {

                            final File file = new File(AthionPlots.configpath + "/schematics/" + args[2] + ".schematic");

                            if (file.exists()) {

                                if (a1 != null) {

                                    if ((plot.owner == p.getName()) || AthionPlots.cPerms(p, "plotme.admin.schematic.other")) {
                                        try {
                                            AthionCore.clear(world, plot);
                                            final SchematicFormat schematic = SchematicFormat.getFormat(new File(AthionPlots.configpath + "/schematics/", args[2] + ".schematic"));
                                            final CuboidClipboard clipboard = schematic.load(new File(AthionPlots.configpath + "/schematics/", args[2] + ".schematic"));
                                            clipboard.setOrigin(vector);
                                            clipboard.paste(localSession.createEditSession(we.wrapPlayer(p)), vector, true);
                                            p.teleport(AthionCore.getPlotHome(p.getLocation().getWorld(), plot));
                                            p.sendMessage(ChatColor.YELLOW + args[2] + " " + ChatColor.GREEN + "Pasted @ ");
                                            p.sendMessage(ChatColor.GRAY + "Plot: " + ChatColor.RESET + x + ";" + z);
                                            p.sendMessage(ChatColor.GRAY + "X: " + ChatColor.RESET + xN);
                                            p.sendMessage(ChatColor.GRAY + "Z: " + ChatColor.RESET + zN);
                                        } catch (final IOException e) {
                                            e.printStackTrace();
                                        } catch (final DataException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        } catch (final MaxChangedBlocksException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    } else {
                                        p.sendMessage(ChatColor.RED + "You cannot load a schematic onto another players plot.");
                                    }

                                }

                            } else {
                                p.sendMessage(ChatColor.RED + "The schematic " + args[2] + " does not exist.");
                            }

                        } else {
                            AthionCommands.SendMsg(cs, "You may use "
                            + ChatColor.YELLOW
                            + "load "
                            + ChatColor.RESET
                            + "To load a specific schematic file."
                            + ChatColor.GRAY
                            + "Example: /ap schematic load testSchematic");
                        }

                    } else {
                        AthionCommands.SendMsg(cs, "You may use "
                        + ChatColor.YELLOW
                        + "load "
                        + ChatColor.RESET
                        + "To load a specific schematic file. "
                        + ChatColor.GRAY
                        + "Example: /ap schematic load testSchematic");
                    }

                }

                AthionPlots.addIgnoreWELimit(p);
            } else {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
            }
        }
    }

}
