package net.athion.athionplots.Listeners;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionPlot;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class AthionListenerWE implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        boolean changemask = false;
        final Player p = event.getPlayer();

        if (to == null) {
            AthionPlots.athionworldedit.removeMask(p);
        } else {
            if (from != null) {
                if (!from.getWorld().getName().equalsIgnoreCase(to.getWorld().getName())) {
                    changemask = true;
                } else if ((from.getBlockX() != to.getBlockX()) || (from.getBlockZ() != to.getBlockZ())) {
                    final String idFrom = AthionCore.getPlotID(from);
                    final String idTo = AthionCore.getPlotID(to);

                    if (!idFrom.equalsIgnoreCase(idTo)) {
                        changemask = true;
                    }
                }
            }

            if (changemask) {
                if (AthionCore.isPlotWorld(to.getWorld())) {
                    if (!AthionPlots.isIgnoringWELimit(p)) {
                        AthionPlots.athionworldedit.setMask(p);
                    } else {
                        AthionPlots.athionworldedit.removeMask(p);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        if (AthionCore.isPlotWorld(p)) {
            if (!AthionPlots.isIgnoringWELimit(p)) {
                AthionPlots.athionworldedit.setMask(p);
            } else {
                AthionPlots.athionworldedit.removeMask(p);
            }
        } else {
            AthionPlots.athionworldedit.removeMask(p);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Player p = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (to == null) {
            AthionPlots.athionworldedit.removeMask(p);
        } else {
            if ((from != null) && AthionCore.isPlotWorld(from) && !AthionCore.isPlotWorld(to)) {
                AthionPlots.athionworldedit.removeMask(p);
            } else if (AthionCore.isPlotWorld(to)) {
                AthionPlots.athionworldedit.setMask(p);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        final Player p = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (to == null) {
            AthionPlots.athionworldedit.removeMask(p);
        } else {
            if ((from != null) && AthionCore.isPlotWorld(from) && !AthionCore.isPlotWorld(to)) {
                AthionPlots.athionworldedit.removeMask(p);
            } else if (AthionCore.isPlotWorld(to)) {
                AthionPlots.athionworldedit.setMask(p);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
        final Player p = event.getPlayer();

        if (AthionCore.isPlotWorld(p) && !AthionPlots.isIgnoringWELimit(p)) {
            final String lowerCaseMessage = event.getMessage().toLowerCase();
            if (lowerCaseMessage.startsWith("/gmask") || lowerCaseMessage.startsWith("//gmask") || lowerCaseMessage.startsWith("/worldedit:gmask") || lowerCaseMessage.startsWith("/worldedit:/gmask")) {
                event.setCancelled(true);
            } else if (lowerCaseMessage.startsWith("/up") || lowerCaseMessage.startsWith("//up") || lowerCaseMessage.startsWith("/worldedit:up") || lowerCaseMessage.startsWith("/worldedit:/up")) {
                final AthionPlot plot = AthionCore.getPlotById(p);

                if ((plot == null) || !plot.isAllowed(p.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();

        if (!AthionPlots.cPerms(p, "AthionPlots.admin.buildanywhere") && AthionCore.isPlotWorld(p) && !AthionPlots.isIgnoringWELimit(p)) {
            if (((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) && (p.getItemInHand() != null) && (p.getItemInHand().getType() != Material.AIR)) {
                final Block b = event.getClickedBlock();
                final AthionPlot plot = AthionCore.getPlotById(b);

                if ((plot != null) && plot.isAllowed(p.getUniqueId())) {
                    AthionPlots.athionworldedit.setMask(p, b.getLocation());
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

}
