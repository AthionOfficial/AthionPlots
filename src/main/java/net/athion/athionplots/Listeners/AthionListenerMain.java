package net.athion.athionplots.Listeners;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionPlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AthionListenerMain implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block b = event.getBlock();

        if (AthionCore.isPlotWorld(b)) {
            final Player p = event.getPlayer();
            final Location loc = b.getLocation();
            final AthionMaps pmi = AthionCore.getMap(event.getBlock().getLocation());
            final boolean canbuild = AthionPlots.cPerms(event.getPlayer(), "plotme.admin.buildanywhere");
            final String id = AthionCore.getPlotID(b.getLocation());

            /*
             * Check if Road & Permissions
             */
            final int valx = loc.getBlockX();
            final int valz = loc.getBlockZ();

            final int size = pmi.PlotSize + pmi.PathWidth;
            final int pathsize = pmi.PathWidth;
            boolean road = false;

            double n3;
            int mod2 = 0;
            final int mod1 = 1;

            Math.ceil((double) valx / size);
            Math.ceil((double) valz / size);

            Math.ceil((double) valx / size);
            Math.ceil((double) valz / size);

            if ((pathsize % 2) == 1) {
                n3 = Math.ceil(((double) pathsize) / 2); //3 7
                mod2 = -1;
            } else {
                n3 = Math.floor(((double) pathsize) / 2); //3 7
            }

            for (double i = n3; i >= 0; i--) {
                if (((((valx - i) + mod1) % size) == 0) || (((valx + i + mod2) % size) == 0)) {
                    road = true;
                }
                if (((((valz - i) + mod1) % size) == 0) || (((valz + i + mod2) % size) == 0)) {
                    road = true;
                }
            }

            if (id.equalsIgnoreCase("")) {
                if (!canbuild) {
                    p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                    event.setCancelled(true);
                }
            } else {
                final AthionPlot plot = AthionCore.getMap(p).plots.get(id);
                if (road) {
                    if (!AthionPlots.cPerms(p, "plotme.use.roadmod")) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                } else if (plot == null) {
                    if (!canbuild) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                } else if (!plot.isAllowed(p.getUniqueId())) {
                    if (!canbuild) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                } else {
                    plot.resetExpire(AthionCore.getMap(b).DaysToExpiration);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block b = event.getBlock();

        if (AthionCore.isPlotWorld(b)) {
            final Player p = event.getPlayer();
            final Location loc = b.getLocation();
            final AthionMaps pmi = AthionCore.getMap(loc);
            final boolean canbuild = AthionPlots.cPerms(p, "plotme.admin.buildanywhere");
            final String id = AthionCore.getPlotID(b.getLocation());

            /*
             * Check if Road & Permissions
             */
            final int valx = loc.getBlockX();
            final int valz = loc.getBlockZ();

            final int size = pmi.PlotSize + pmi.PathWidth;
            final int pathsize = pmi.PathWidth;
            boolean road = false;

            double n3;
            int mod2 = 0;
            final int mod1 = 1;

            Math.ceil((double) valx / size);
            Math.ceil((double) valz / size);

            Math.ceil((double) valx / size);
            Math.ceil((double) valz / size);

            if ((pathsize % 2) == 1) {
                n3 = Math.ceil(((double) pathsize) / 2); //3 7
                mod2 = -1;
            } else {
                n3 = Math.floor(((double) pathsize) / 2); //3 7
            }

            for (double i = n3; i >= 0; i--) {
                if (((((valx - i) + mod1) % size) == 0) || (((valx + i + mod2) % size) == 0)) {
                    road = true;
                }
                if (((((valz - i) + mod1) % size) == 0) || (((valz + i + mod2) % size) == 0)) {
                    road = true;
                }
            }

            if (id.equalsIgnoreCase("")) {
                if (!canbuild) {
                    p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                    event.setCancelled(true);
                }
            } else {
                final AthionPlot plot = AthionCore.getPlotById(p, id);
                if (road) {
                    if (!AthionPlots.cPerms(p, "plotme.use.roadmod")) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                } else if (plot == null) {
                    if (!canbuild) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                } else if (!plot.isAllowed(p.getUniqueId())) {
                    if (!canbuild) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                } else {
                    plot.resetExpire(AthionCore.getMap(b).DaysToExpiration);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityChangeBlock(final EntityChangeBlockEvent event) {
        final Block b = event.getBlock();
        final Entity e = event.getEntity();

        if (AthionCore.isPlotWorld(b)) {
            if (!(e instanceof Player)) {
                if (!(e instanceof org.bukkit.entity.FallingBlock)) {
                    event.setCancelled(true);
                }
            } else {
                final Player p = (Player) e;
                final boolean canbuild = AthionPlots.cPerms(p, "plotme.admin.buildanywhere");
                final String id = AthionCore.getPlotID(b.getLocation());

                if (id.equalsIgnoreCase("")) {
                    if (!canbuild) {
                        event.setCancelled(true);
                    }
                } else {
                    final AthionPlot plot = AthionCore.getPlotById(p, id);

                    if (plot == null) {
                        if (!canbuild) {
                            event.setCancelled(true);
                        }
                    } else if (!plot.isAllowed(p.getUniqueId())) {
                        if (!canbuild) {
                            event.setCancelled(true);
                        }
                    } else {
                        plot.resetExpire(AthionCore.getMap(b).DaysToExpiration);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityBlockForm(final EntityBlockFormEvent event) {
        final Block b = event.getBlock();
        final Entity e = event.getEntity();

        if (AthionCore.isPlotWorld(b)) {
            if (!(e instanceof Player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
        if (!AthionPlots.cPerms(event.getPlayer(), "plotme.admin.buildanywhere")) {
            final BlockFace bf = event.getBlockFace();
            final Block b = event.getBlockClicked().getLocation().add(bf.getModX(), bf.getModY(), bf.getModZ()).getBlock();
            if (AthionCore.isPlotWorld(b)) {
                final String id = AthionCore.getPlotID(b.getLocation());
                final Player p = event.getPlayer();

                if (id.equalsIgnoreCase("")) {
                    p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                    event.setCancelled(true);
                } else {
                    final AthionPlot plot = AthionCore.getPlotById(p, id);

                    if (plot == null) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    } else if (!plot.isAllowed(p.getUniqueId())) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerBucketFill(final PlayerBucketFillEvent event) {
        if (!AthionPlots.cPerms(event.getPlayer(), "plotme.admin.buildanywhere")) {
            final Block b = event.getBlockClicked();
            if (AthionCore.isPlotWorld(b)) {
                final String id = AthionCore.getPlotID(b.getLocation());
                final Player p = event.getPlayer();

                if (id.equalsIgnoreCase("")) {
                    p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                    event.setCancelled(true);
                } else {
                    final AthionPlot plot = AthionCore.getPlotById(p, id);

                    if (plot == null) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    } else if (!plot.isAllowed(p.getUniqueId())) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Block b = event.getClickedBlock();

        if (AthionCore.isPlotWorld(b)) {
            final AthionMaps pmi = AthionCore.getMap(b);
            boolean blocked = false;
            final Player player = event.getPlayer();
            final boolean canbuild = AthionPlots.cPerms(player, "plotme.admin.buildanywhere");

            if (event.isBlockInHand() && (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                final ItemStack is = player.getItemInHand();

                if ((event.getClickedBlock() != null) && (is != null)) {
                    final Material matClicked = event.getClickedBlock().getType();
                    final Material matHeld = is.getType();

                    if (matClicked == matHeld) {
                        final BlockFace face = event.getBlockFace();
                        final Block builtblock = b.getWorld().getBlockAt(b.getX() + face.getModX(), b.getY() + face.getModY(), b.getZ() + face.getModZ());

                        final String id = AthionCore.getPlotID(builtblock.getLocation());

                        final Player p = event.getPlayer();

                        if (id.equalsIgnoreCase("")) {
                            if (!canbuild) {
                                p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                                event.setCancelled(true);
                            }
                        } else {
                            final AthionPlot plot = AthionCore.getPlotById(p, id);

                            if (plot == null) {
                                if (!canbuild) {
                                    p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                                    event.setCancelled(true);
                                }
                            } else {
                                if (!plot.isAllowed(p.getName())) {
                                    if (!canbuild) {
                                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                                        event.setCancelled(true);
                                    }
                                } else {
                                    plot.resetExpire(AthionCore.getMap(b).DaysToExpiration);
                                }
                            }
                        }
                    }
                }
            } else {
                if (pmi.ProtectedBlocks.contains(b.getTypeId())) {
                    if (!AthionPlots.cPerms(player, "plotme.unblock." + b.getTypeId())) {
                        blocked = true;
                    }
                }

                final ItemStack is = event.getItem();

                if ((is != null) && (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                    final int itemid = is.getType().getId();
                    final byte itemdata = is.getData().getData();

                    if (pmi.PreventedItems.contains("" + itemid) || pmi.PreventedItems.contains("" + itemid + ":" + itemdata)) {
                        if (!AthionPlots.cPerms(player, "plotme.unblock." + itemid)) {
                            blocked = true;
                        }
                    }
                }

                if (blocked) {
                    final String id = AthionCore.getPlotID(b.getLocation());

                    final Player p = event.getPlayer();

                    if (id.equalsIgnoreCase("")) {
                        if (!canbuild) {
                            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                p.sendMessage(AthionPlots.caption("ErrCannotUse"));
                            }
                            event.setCancelled(true);
                        }
                    } else {
                        final AthionPlot plot = AthionCore.getPlotById(p, id);

                        if (plot == null) {
                            if (!canbuild) {
                                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                    p.sendMessage(AthionPlots.caption("ErrCannotUse"));
                                }
                                event.setCancelled(true);
                            }
                        } else if (!plot.isAllowed(p.getName())) {
                            if (!canbuild) {
                                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                    p.sendMessage(AthionPlots.caption("ErrCannotUse"));
                                }
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockSpread(final BlockSpreadEvent event) {
        final Block b = event.getBlock();

        if (AthionCore.isPlotWorld(b)) {
            final String id = AthionCore.getPlotID(b.getLocation());

            if (id.equalsIgnoreCase("")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockForm(final BlockFormEvent event) {
        final Block b = event.getBlock();

        if (AthionCore.isPlotWorld(b)) {
            final String id = AthionCore.getPlotID(b.getLocation());

            if (id.equalsIgnoreCase("")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockDamage(final BlockDamageEvent event) {
        final Block b = event.getBlock();

        if (AthionCore.isPlotWorld(b)) {
            final String id = AthionCore.getPlotID(b.getLocation());

            if (id.equalsIgnoreCase("")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFade(final BlockFadeEvent event) {
        final Block b = event.getBlock();

        if (AthionCore.isPlotWorld(b)) {
            final String id = AthionCore.getPlotID(b.getLocation());

            if (id.equalsIgnoreCase("")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFromTo(final BlockFromToEvent event) {
        final Block b = event.getToBlock();

        if (AthionCore.isPlotWorld(b)) {
            final String id = AthionCore.getPlotID(b.getLocation());

            if (id.equalsIgnoreCase("")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockGrow(final BlockGrowEvent event) {
        final Block b = event.getBlock();

        if (AthionCore.isPlotWorld(b)) {
            final String id = AthionCore.getPlotID(b.getLocation());

            if (id.equalsIgnoreCase("")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
        if (AthionCore.isPlotWorld(event.getBlock())) {
            final BlockFace face = event.getDirection();

            for (final Block b : event.getBlocks()) {
                final String id = AthionCore.getPlotID(b.getLocation().add(face.getModX(), face.getModY(), face.getModZ()));

                if (id.equalsIgnoreCase("")) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
        final Block b = event.getRetractLocation().getBlock();

        if (AthionCore.isPlotWorld(b) && (event.getBlock().getType() == Material.PISTON_STICKY_BASE)) {
            final String id = AthionCore.getPlotID(b.getLocation());

            if (id.equalsIgnoreCase("")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onStructureGrow(final StructureGrowEvent event) {
        final List<BlockState> blocks = event.getBlocks();
        boolean found = false;

        for (int i = 0; i < blocks.size(); i++) {
            if (found || AthionCore.isPlotWorld(blocks.get(i))) {
                found = true;
                final String id = AthionCore.getPlotID(blocks.get(i).getLocation());

                if (id.equalsIgnoreCase("")) {
                    event.getBlocks().remove(i);
                    i--;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(final EntityExplodeEvent event) {
        final Location l = event.getLocation();

        if (l != null) {
            final AthionMaps pmi = AthionCore.getMap(l);

            if ((pmi != null) && pmi.DisableExplosion) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        final Block b = event.getBlock();

        if (b != null) {
            final AthionMaps pmi = AthionCore.getMap(b);

            if (pmi != null) {
                if (pmi.DisableIgnition) {
                    event.setCancelled(true);
                } else {
                    final String id = AthionCore.getPlotID(b.getLocation());
                    final Player p = event.getPlayer();

                    if (id.equalsIgnoreCase("") || (p == null)) {
                        event.setCancelled(true);
                    } else {
                        final AthionPlot plot = AthionCore.getPlotById(b, id);

                        if (plot == null) {
                            event.setCancelled(true);
                        } else if (!plot.isAllowed(p.getUniqueId())) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingPlace(final HangingPlaceEvent event) {
        final Block b = event.getBlock();

        if (AthionCore.isPlotWorld(b)) {
            final String id = AthionCore.getPlotID(b.getLocation());
            final Player p = event.getPlayer();
            final boolean canbuild = AthionPlots.cPerms(event.getPlayer(), "plotme.admin.buildanywhere");

            if (id.equalsIgnoreCase("")) {
                if (!canbuild) {
                    p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                    event.setCancelled(true);
                }
            } else {
                final AthionPlot plot = AthionCore.getPlotById(p, id);

                if (plot == null) {
                    if (!canbuild) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                } else if (!plot.isAllowed(p.getUniqueId())) {
                    if (!canbuild) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                } else {
                    plot.resetExpire(AthionCore.getMap(b).DaysToExpiration);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingBreakByEntity(final HangingBreakByEntityEvent event) {
        final Entity entity = event.getRemover();

        if (entity instanceof Player) {
            final Player p = (Player) entity;

            final boolean canbuild = AthionPlots.cPerms(p, "plotme.admin.buildanywhere");

            final Location l = event.getEntity().getLocation();

            if (AthionCore.isPlotWorld(l)) {
                final String id = AthionCore.getPlotID(l);

                if (id.equalsIgnoreCase("")) {
                    if (!canbuild) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                } else {
                    final AthionPlot plot = AthionCore.getPlotById(p, id);

                    if (plot == null) {
                        if (!canbuild) {
                            p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                            event.setCancelled(true);
                        }
                    } else if (!plot.isAllowed(p.getUniqueId())) {
                        if (!canbuild) {
                            p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                            event.setCancelled(true);
                        }
                    } else {
                        plot.resetExpire(AthionCore.getMap(l).DaysToExpiration);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        final Location l = event.getRightClicked().getLocation();

        if (AthionCore.isPlotWorld(l)) {
            final Player p = event.getPlayer();
            final boolean canbuild = AthionPlots.cPerms(p, "plotme.admin.buildanywhere");
            final String id = AthionCore.getPlotID(l);

            if (id.equalsIgnoreCase("")) {
                if (!canbuild) {
                    p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                    event.setCancelled(true);
                }
            } else {
                final AthionPlot plot = AthionCore.getPlotById(p, id);

                if (plot == null) {
                    if (!canbuild) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                } else if (!plot.isAllowed(p.getUniqueId())) {
                    if (!canbuild) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                } else {
                    plot.resetExpire(AthionCore.getMap(l).DaysToExpiration);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Location l = event.getEntity().getLocation();
        final Entity e = event.getDamager();

        if (AthionCore.isPlotWorld(l)) {
            if (!(e instanceof Player)) {
                event.setCancelled(true);
            } else {
                final Player p = (Player) e;
                final boolean canbuild = AthionPlots.cPerms(p, "plotme.admin.buildanywhere");
                final String id = AthionCore.getPlotID(l);

                if (id.equalsIgnoreCase("")) {
                    if (!canbuild) {
                        p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                        event.setCancelled(true);
                    }
                } else {
                    final AthionPlot plot = AthionCore.getPlotById(p, id);

                    if (plot == null) {
                        if (!canbuild) {
                            p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                            event.setCancelled(true);
                        }
                    } else if (!plot.isAllowed(p.getUniqueId())) {
                        if (!canbuild) {
                            p.sendMessage(AthionPlots.caption("ErrCannotBuild"));
                            event.setCancelled(true);
                        }
                    } else {
                        plot.resetExpire(AthionCore.getMap(l).DaysToExpiration);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerEggThrow(final PlayerEggThrowEvent event) {
        final Location l = event.getEgg().getLocation();

        if (AthionCore.isPlotWorld(l)) {
            final Player p = event.getPlayer();
            final boolean canbuild = AthionPlots.cPerms(p, "plotme.admin.buildanywhere");
            final String id = AthionCore.getPlotID(l);

            if (id.equalsIgnoreCase("")) {
                if (!canbuild) {
                    p.sendMessage(AthionPlots.caption("ErrCannotUseEggs"));
                    event.setHatching(false);
                }
            } else {
                final AthionPlot plot = AthionCore.getPlotById(p, id);

                if (plot == null) {
                    if (!canbuild) {
                        p.sendMessage(AthionPlots.caption("ErrCannotUseEggs"));
                        event.setHatching(false);
                    }
                } else if (!plot.isAllowed(p.getUniqueId())) {
                    if (!canbuild) {
                        p.sendMessage(AthionPlots.caption("ErrCannotUseEggs"));
                        event.setHatching(false);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player p = event.getPlayer();

        if (p != null) {
            AthionCore.UpdatePlayerNameFromId(p.getUniqueId(), p.getName());
        }
    }

    @EventHandler
    public void onFoodChange(final FoodLevelChangeEvent event) {
        event.setCancelled(true);
        for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.getFoodLevel() != 20) {
                p.setFoodLevel(20);
            }
        }
    }

}
