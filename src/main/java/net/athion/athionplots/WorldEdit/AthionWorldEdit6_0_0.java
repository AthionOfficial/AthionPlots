package net.athion.athionplots.WorldEdit;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionPlot;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.RegionMask;
import com.sk89q.worldedit.regions.CuboidRegion;

public class AthionWorldEdit6_0_0 implements AthionWorldEdit {

    @Override
    public void setMask(final Player p) {
        setMask(p, p.getLocation());
    }

    @Override
    public void setMask(final Player p, final Location l) {
        final World w = p.getWorld();

        final String id = AthionCore.getPlotID(l);

        Location bottom = null;
        Location top = null;

        final LocalSession session = AthionPlots.worldeditplugin.getSession(p);

        if (!id.equalsIgnoreCase("")) {
            final AthionPlot plot = AthionCore.getPlotById(p, id);

            if ((plot != null) && plot.isAllowed(p.getUniqueId())) {
                bottom = AthionCore.getPlotBottomLoc(w, id);
                top = AthionCore.getPlotTopLoc(w, id);

                final LocalSession localsession = AthionPlots.worldeditplugin.getSession(p);
                final com.sk89q.worldedit.world.World world = localsession.getSelectionWorld();

                final Vector pos1 = new Vector(bottom.getBlockX(), bottom.getBlockY(), bottom.getBlockZ());
                final Vector pos2 = new Vector(top.getBlockX(), top.getBlockY(), top.getBlockZ());

                final CuboidRegion cr = new CuboidRegion(world, pos1, pos2);

                final RegionMask rm = new RegionMask(cr);

                session.setMask(rm);
                return;
            }
        }

        if ((bottom == null) || (top == null)) {
            bottom = new Location(w, 0, 0, 0);
            top = new Location(w, 0, 0, 0);
        }

        if (session.getMask() == null) {
            final LocalSession localsession = AthionPlots.worldeditplugin.getSession(p);
            final com.sk89q.worldedit.world.World world = localsession.getSelectionWorld();

            final Vector pos1 = new Vector(bottom.getBlockX(), bottom.getBlockY(), bottom.getBlockZ());
            final Vector pos2 = new Vector(top.getBlockX(), top.getBlockY(), top.getBlockZ());

            final CuboidRegion cr = new CuboidRegion(world, pos1, pos2);

            final RegionMask rm = new RegionMask(cr);

            session.setMask(rm);
        }
    }

    @Override
    public void removeMask(final Player p) {
        final LocalSession session = AthionPlots.worldeditplugin.getSession(p);
        final Mask mask = null;
        session.setMask(mask);
    }
}
