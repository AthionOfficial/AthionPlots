package net.athion.athionplots.Listeners;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionPlot;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class AthionListenerBlock implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player p = event.getPlayer();

        if (AthionCore.isPlotWorld(p) && !AthionPlots.cPerms(p, "plotme.admin.bypassdeny")) {
            final Location to = event.getTo();

            final String idTo = AthionCore.getPlotID(to);

            if (!idTo.equalsIgnoreCase("")) {
                final AthionPlot plot = AthionCore.getPlotById(p, idTo);

                if ((plot != null) && plot.isDenied(p.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Player p = event.getPlayer();

        if (AthionCore.isPlotWorld(p) && !AthionPlots.cPerms(p, "plotme.admin.bypassdeny")) {
            final Location to = event.getTo();

            final String idTo = AthionCore.getPlotID(to);

            if (!idTo.equalsIgnoreCase("")) {
                final AthionPlot plot = AthionCore.getPlotById(p, idTo);

                if ((plot != null) && plot.isDenied(p.getUniqueId())) {
                    event.setTo(AthionCore.getPlotHome(p.getWorld(), plot));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player p = event.getPlayer();

        if (AthionCore.isPlotWorld(p) && !AthionPlots.cPerms(p, "plotme.admin.bypassdeny")) {
            final String id = AthionCore.getPlotId(p);

            if (!id.equalsIgnoreCase("")) {
                final AthionPlot plot = AthionCore.getPlotById(p, id);

                if ((plot != null) && plot.isDenied(p.getUniqueId())) {
                    p.teleport(AthionCore.getPlotHome(p.getWorld(), plot));
                }
            }
        }
    }
}
