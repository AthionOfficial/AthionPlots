package net.athion.athionplots.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.athion.athionplots.AthionPlots;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class AthionCore {

    public static String getPlotID(final Location loc) {
        final AthionMaps pmi = getMap(loc);

        if (pmi != null) {
            final int valx = loc.getBlockX();
            final int valz = loc.getBlockZ();

            final int size = pmi.PlotSize + pmi.PathWidth;
            final int pathsize = pmi.PathWidth;
            boolean road = false;

            double n3;
            int mod2 = 0;
            final int mod1 = 1;

            int x = (int) Math.ceil((double) valx / size);
            int z = (int) Math.ceil((double) valz / size);

            int x2 = (int) Math.ceil((double) valx / size);
            int z2 = (int) Math.ceil((double) valz / size);

            if ((pathsize % 2) == 1) {
                n3 = Math.ceil(((double) pathsize) / 2); //3 7
                mod2 = -1;
            } else {
                n3 = Math.floor(((double) pathsize) / 2); //3 7
            }

            for (double i = n3; i >= 0; i--) {
                if (((((valx - i) + mod1) % size) == 0) || (((valx + i + mod2) % size) == 0)) {
                    road = true;

                    x = (int) Math.ceil((valx - n3) / size);
                    x2 = (int) Math.ceil((valx + n3) / size);
                }
                if (((((valz - i) + mod1) % size) == 0) || (((valz + i + mod2) % size) == 0)) {
                    road = true;

                    z = (int) Math.ceil((valz - n3) / size);
                    z2 = (int) Math.ceil((valz + n3) / size);
                }
            }

            if (road) {
                final String id1 = x + ";" + z;
                final String id2 = x2 + ";" + z2;
                final String id3 = x + ";" + z2;
                final String id4 = x2 + ";" + z;

                final HashMap<String, AthionPlot> plots = pmi.plots;

                final AthionPlot p1 = plots.get(id1);
                final AthionPlot p2 = plots.get(id2);
                final AthionPlot p3 = plots.get(id3);
                final AthionPlot p4 = plots.get(id4);

                if ((p1 == null)
                || (p2 == null)
                || (p3 == null)
                || (p4 == null)
                || !p1.owner.equalsIgnoreCase(p2.owner)
                || !p2.owner.equalsIgnoreCase(p3.owner)
                || !p3.owner.equalsIgnoreCase(p4.owner)) {
                    return "";
                } else {
                    //adjustLinkedPlots(id1, loc.getWorld());
                    return id1;
                }
            } else {
                return "" + x + ";" + z;
                //return "";
            }
        } else {
            return "";
        }
    }

    public static boolean mP(final World w, final String idFrom, final String idTo) {
        final Location plot1Bottom = getPlotBottomLoc(w, idFrom);
        final Location plot2Bottom = getPlotBottomLoc(w, idTo);
        final Location plot1Top = getPlotTopLoc(w, idFrom);

        final int distanceX = plot1Bottom.getBlockX() - plot2Bottom.getBlockX();
        final int distanceZ = plot1Bottom.getBlockZ() - plot2Bottom.getBlockZ();

        for (int x = plot1Bottom.getBlockX(); x <= plot1Top.getBlockX(); x++) {
            for (int z = plot1Bottom.getBlockZ(); z <= plot1Top.getBlockZ(); z++) {
                Block plot1Block = w.getBlockAt(new Location(w, x, 0, z));
                Block plot2Block = w.getBlockAt(new Location(w, x - distanceX, 0, z - distanceZ));

                final String plot1Biome = plot1Block.getBiome().name();
                final String plot2Biome = plot2Block.getBiome().name();

                plot1Block.setBiome(Biome.valueOf(plot2Biome));
                plot2Block.setBiome(Biome.valueOf(plot1Biome));

                for (int y = 0; y < w.getMaxHeight(); y++) {
                    plot1Block = w.getBlockAt(new Location(w, x, y, z));
                    final int plot1Type = plot1Block.getTypeId();
                    final byte plot1Data = plot1Block.getData();

                    plot2Block = w.getBlockAt(new Location(w, x - distanceX, y, z - distanceZ));

                    final int plot2Type = plot2Block.getTypeId();
                    final byte plot2Data = plot2Block.getData();

                    //plot1Block.setTypeId(plot2Type);
                    plot1Block.setTypeIdAndData(plot2Type, plot2Data, false);
                    plot1Block.setData(plot2Data);

                    //net.minecraft.server.World world = ((org.bukkit.craftbukkit.CraftWorld) w).getHandle();
                    //world.setRawTypeIdAndData(plot1Block.getX(), plot1Block.getY(), plot1Block.getZ(), plot2Type, plot2Data);

                    //plot2Block.setTypeId(plot1Type);
                    plot2Block.setTypeIdAndData(plot1Type, plot1Data, false);
                    plot2Block.setData(plot1Data);
                    //world.setRawTypeIdAndData(plot2Block.getX(), plot2Block.getY(), plot2Block.getZ(), plot1Type, plot1Data);
                }
            }
        }

        final HashMap<String, AthionPlot> plots = getPlots(w);

        if (plots.containsKey(idFrom)) {
            if (plots.containsKey(idTo)) {
                final AthionPlot plot1 = plots.get(idFrom);
                final AthionPlot plot2 = plots.get(idTo);

                int idX = getIdX(idTo);
                int idZ = getIdZ(idTo);
                AthionSQL.deletePlot(idX, idZ, plot2.world);
                plots.remove(idFrom);
                plots.remove(idTo);
                idX = getIdX(idFrom);
                idZ = getIdZ(idFrom);
                AthionSQL.deletePlot(idX, idZ, plot1.world);

                plot2.id = "" + idX + ";" + idZ;
                AthionSQL.addPlot(plot2, idX, idZ, w);
                plots.put(idFrom, plot2);

                for (int i = 0; i < plot2.comments.size(); i++) {
                    String strUUID = "";
                    UUID uuid = null;

                    if (plot2.comments.get(i).length >= 3) {
                        strUUID = plot2.comments.get(i)[2];
                        try {
                            uuid = UUID.fromString(strUUID);
                        } catch (final Exception e) {}
                    }
                    AthionSQL.addPlotComment(plot2.comments.get(i), i, idX, idZ, plot2.world, uuid);
                }

                for (final String player : plot2.allowed()) {
                    AthionSQL.addPlotAllowed(player, idX, idZ, plot2.world);
                }

                idX = getIdX(idTo);
                idZ = getIdZ(idTo);
                plot1.id = "" + idX + ";" + idZ;
                AthionSQL.addPlot(plot1, idX, idZ, w);
                plots.put(idTo, plot1);

                for (int i = 0; i < plot1.comments.size(); i++) {
                    String strUUID = "";
                    UUID uuid = null;

                    if (plot1.comments.get(i).length >= 3) {
                        strUUID = plot1.comments.get(i)[2];
                        try {
                            uuid = UUID.fromString(strUUID);
                        } catch (final Exception e) {}
                    }

                    AthionSQL.addPlotComment(plot1.comments.get(i), i, idX, idZ, plot1.world, uuid);
                }

                for (final String player : plot1.allowed()) {
                    AthionSQL.addPlotAllowed(player, idX, idZ, plot1.world);
                }

                setOwnerSign(w, plot1);
                setSellSign(w, plot1);
                setOwnerSign(w, plot2);
                setSellSign(w, plot2);

            } else {
                final AthionPlot plot = plots.get(idFrom);

                int idX = getIdX(idFrom);
                int idZ = getIdZ(idFrom);
                AthionSQL.deletePlot(idX, idZ, plot.world);
                plots.remove(idFrom);
                idX = getIdX(idTo);
                idZ = getIdZ(idTo);
                plot.id = "" + idX + ";" + idZ;
                AthionSQL.addPlot(plot, idX, idZ, w);
                plots.put(idTo, plot);

                for (int i = 0; i < plot.comments.size(); i++) {
                    String strUUID = "";
                    UUID uuid = null;

                    if (plot.comments.get(i).length >= 3) {
                        strUUID = plot.comments.get(i)[2];
                        try {
                            uuid = UUID.fromString(strUUID);
                        } catch (final Exception e) {}
                    }
                    AthionSQL.addPlotComment(plot.comments.get(i), i, idX, idZ, plot.world, uuid);
                }

                for (final String player : plot.allowed()) {
                    AthionSQL.addPlotAllowed(player, idX, idZ, plot.world);
                }

                setOwnerSign(w, plot);
                setSellSign(w, plot);
                removeOwnerSign(w, idFrom);
                removeSellSign(w, idFrom);

            }
        } else {
            if (plots.containsKey(idTo)) {
                final AthionPlot plot = plots.get(idTo);

                int idX = getIdX(idTo);
                int idZ = getIdZ(idTo);
                AthionSQL.deletePlot(idX, idZ, plot.world);
                plots.remove(idTo);

                idX = getIdX(idFrom);
                idZ = getIdZ(idFrom);
                plot.id = "" + idX + ";" + idZ;
                AthionSQL.addPlot(plot, idX, idZ, w);
                plots.put(idFrom, plot);

                for (int i = 0; i < plot.comments.size(); i++) {
                    String strUUID = "";
                    UUID uuid = null;

                    if (plot.comments.get(i).length >= 3) {
                        strUUID = plot.comments.get(i)[2];
                        try {
                            uuid = UUID.fromString(strUUID);
                        } catch (final Exception e) {}
                    }
                    AthionSQL.addPlotComment(plot.comments.get(i), i, idX, idZ, plot.world, uuid);
                }

                for (final String player : plot.allowed()) {
                    AthionSQL.addPlotAllowed(player, idX, idZ, plot.world);
                }

                setOwnerSign(w, plot);
                setSellSign(w, plot);
                removeOwnerSign(w, idTo);
                removeSellSign(w, idTo);
            }
        }

        return true;
    }

    public static AthionPlot claimPlot(final World world, final String id, final String owner, final UUID uuid) {
        if (isPlotAvailable(id, world) && (id != "")) {
            final AthionPlot plot = new AthionPlot(owner, uuid, getPlotTopLoc(world, id), getPlotBottomLoc(world, id), id, getMap(world).DaysToExpiration);

            setOwnerSign(world, plot);

            getPlots(world).put(id, plot);
            AthionSQL.addPlot(plot, getIdX(id), getIdZ(id), world);
            return plot;
        } else {
            return null;
        }
    }

    public static void setOwnerSign(final World world, final AthionPlot plot) {
        final Location pillar = new Location(world, bottomX(plot.id, world) - 1, getMap(world).RoadHeight + 1, bottomZ(plot.id, world) - 1);

        final Block bsign = pillar.add(0, 0, -1).getBlock();
        final Block redlight = pillar.add(0, -1, 0).getBlock();
        final Block redtorch = pillar.add(0, -2, 0).getBlock();
        bsign.setType(Material.AIR);
        redlight.setType(Material.REDSTONE_LAMP_ON);
        redtorch.setType(Material.REDSTONE_TORCH_ON);;
        bsign.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 2, false);

        final String id = getPlotID(new Location(world, bottomX(plot.id, world), 0, bottomZ(plot.id, world)));

        final Sign sign = (Sign) bsign.getState();
        if (("Plot: " + ChatColor.DARK_GRAY + id).length() > 16) {
            sign.setLine(0, ("Plot: " + ChatColor.DARK_GRAY + id).substring(0, 16));
            if (("Plot: " + ChatColor.DARK_GRAY + id).length() > 32) {
                sign.setLine(1, ("Plot: " + ChatColor.DARK_GRAY + id).substring(16, 32));
            } else {
                sign.setLine(1, ("Plot: " + ChatColor.DARK_GRAY + id).substring(16));
            }
        } else {
            sign.setLine(0, "Plot: " + ChatColor.DARK_GRAY + id);
        }
        if (("By: " + ChatColor.DARK_GRAY + plot.owner).length() > 16) {
            sign.setLine(2, ("By: " + ChatColor.DARK_GRAY + plot.owner).substring(0, 16));
            if (("By: " + ChatColor.DARK_GRAY + plot.owner).length() > 32) {
                sign.setLine(3, ("By: " + ChatColor.DARK_GRAY + plot.owner).substring(16, 32));
            } else {
                sign.setLine(3, ("By: " + ChatColor.DARK_GRAY + plot.owner).substring(16));
            }
        } else {
            sign.setLine(2, "By: " + ChatColor.DARK_GRAY + plot.owner);
            sign.setLine(3, "");
        }
        sign.update(true);
    }

    public static Location getTop(final World w, final AthionPlot plot) {
        return new Location(w, AthionCore.topX(plot.id, w), w.getMaxHeight(), AthionCore.topZ(plot.id, w));
    }

    public static Location getBottom(final World w, final AthionPlot plot) {
        return new Location(w, AthionCore.bottomX(plot.id, w), 0, AthionCore.bottomZ(plot.id, w));
    }

    public static void clear(final World w, final AthionPlot plot) {
        clear(getBottom(w, plot), getTop(w, plot));

        //regen(w, plot);
    }

    public static void clear(final Location bottom, final Location top) {
        final AthionMaps pmi = getMap(bottom);

        final int bottomX = bottom.getBlockX();
        final int topX = top.getBlockX();
        final int bottomZ = bottom.getBlockZ();
        final int topZ = top.getBlockZ();

        final int minChunkX = (int) Math.floor((double) bottomX / 16);
        final int maxChunkX = (int) Math.floor((double) topX / 16);
        final int minChunkZ = (int) Math.floor((double) bottomZ / 16);
        final int maxChunkZ = (int) Math.floor((double) topZ / 16);

        final World w = bottom.getWorld();

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                final Chunk chunk = w.getChunkAt(cx, cz);

                for (final Entity e : chunk.getEntities()) {
                    final Location eloc = e.getLocation();

                    if (!(e instanceof Player)
                    && (eloc.getBlockX() >= bottom.getBlockX())
                    && (eloc.getBlockX() <= top.getBlockX())
                    && (eloc.getBlockZ() >= bottom.getBlockZ())
                    && (eloc.getBlockZ() <= top.getBlockZ())) {
                        e.remove();
                    }
                }
            }
        }

        for (int x = bottomX; x <= topX; x++) {
            for (int z = bottomZ; z <= topZ; z++) {
                Block block = new Location(w, x, 0, z).getBlock();

                block.setBiome(Biome.PLAINS);

                for (int y = w.getMaxHeight(); y >= 0; y--) {
                    block = new Location(w, x, y, z).getBlock();

                    final BlockState state = block.getState();

                    if (state instanceof InventoryHolder) {
                        final InventoryHolder holder = (InventoryHolder) state;
                        holder.getInventory().clear();
                    }

                    if (state instanceof Jukebox) {
                        final Jukebox jukebox = (Jukebox) state;
                        //Remove once they fix the NullPointerException
                        try {
                            jukebox.setPlaying(Material.AIR);
                        } catch (final Exception e) {}
                    }

                    if (y == 0) {
                        block.setTypeId(pmi.BottomBlockId);
                    } else if (y == pmi.RoadHeight) {
                        block.setTypeId(pmi.PlotFloorBlockId);
                    } else if (y < pmi.RoadHeight) {
                        block.setTypeId(pmi.PlotFillingBlockId);
                    } else {
                        if ((y == (pmi.RoadHeight)) && ((x == (bottomX - 1)) || (x == (topX + 1)) || (z == (bottomZ - 1)) || (z == (topZ + 1)))) {
                            //block.setTypeId(pmi.WallBlockId);
                        } else {
                            block.setTypeIdAndData(0, (byte) 0, false); //.setType(Material.AIR);
                        }
                    }
                }
            }
        }

        adjustWall(bottom);
    }

    public static void adjustWall(final Location l) {
        final AthionPlot plot = getPlotById(l);
        final World w = l.getWorld();
        final AthionMaps pmi = getMap(w);

        final List<String> wallids = new ArrayList<String>();

        final String auctionwallid = pmi.AuctionWallBlockId;
        final String forsalewallid = pmi.ForSaleWallBlockId;

        if (plot.protect) {
            wallids.add(pmi.ProtectedWallBlockId);
        }
        if (plot.auctionned && !wallids.contains(auctionwallid)) {
            wallids.add(auctionwallid);
        }
        if (plot.forsale && !wallids.contains(forsalewallid)) {
            wallids.add(forsalewallid);
        }

        if (wallids.size() == 0) {
            wallids.add("" + pmi.WallBlockId + ":" + pmi.WallBlockValue);
        }

        int ctr = 0;

        final Location bottom = getPlotBottomLoc(w, plot.id);
        final Location top = getPlotTopLoc(w, plot.id);

        int x;
        int z;

        String currentblockid;
        Block block;

        for (x = bottom.getBlockX() - 1; x < (top.getBlockX() + 1); x++) {
            z = bottom.getBlockZ() - 1;
            currentblockid = wallids.get(ctr);
            ctr = (ctr == (wallids.size() - 1)) ? 0 : ctr + 1;
            block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
            setWall(block, currentblockid);
        }

        for (z = bottom.getBlockZ() - 1; z < (top.getBlockZ() + 1); z++) {
            x = top.getBlockX() + 1;
            currentblockid = wallids.get(ctr);
            ctr = (ctr == (wallids.size() - 1)) ? 0 : ctr + 1;
            block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
            setWall(block, currentblockid);
        }

        for (x = top.getBlockX() + 1; x > (bottom.getBlockX() - 1); x--) {
            z = top.getBlockZ() + 1;
            currentblockid = wallids.get(ctr);
            ctr = (ctr == (wallids.size() - 1)) ? 0 : ctr + 1;
            block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
            setWall(block, currentblockid);
        }

        for (z = top.getBlockZ() + 1; z > (bottom.getBlockZ() - 1); z--) {
            x = bottom.getBlockX() - 1;
            currentblockid = wallids.get(ctr);
            ctr = (ctr == (wallids.size() - 1)) ? 0 : ctr + 1;
            block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
            setWall(block, currentblockid);
        }
    }

    private static void setWall(final Block block, final String currentblockid) {

        int blockId;
        byte blockData = 0;
        final AthionMaps pmi = getMap(block);

        if (currentblockid.contains(":")) {
            try {
                blockId = Integer.parseInt(currentblockid.substring(0, currentblockid.indexOf(":")));
                blockData = Byte.parseByte(currentblockid.substring(currentblockid.indexOf(":") + 1));
            } catch (final NumberFormatException e) {
                blockId = pmi.WallBlockId;
                blockData = pmi.WallBlockValue;
            }
        } else {
            try {
                blockId = Integer.parseInt(currentblockid);
            } catch (final NumberFormatException e) {
                blockId = pmi.WallBlockId;
            }
        }

        block.setTypeIdAndData(blockId, blockData, true);
    }

    public static boolean isBlockInPlot(final AthionPlot plot, final Location blocklocation) {
        final World w = blocklocation.getWorld();
        final int lowestX = Math.min(AthionCore.bottomX(plot.id, w), AthionCore.topX(plot.id, w));
        final int highestX = Math.max(AthionCore.bottomX(plot.id, w), AthionCore.topX(plot.id, w));
        final int lowestZ = Math.min(AthionCore.bottomZ(plot.id, w), AthionCore.topZ(plot.id, w));
        final int highestZ = Math.max(AthionCore.bottomZ(plot.id, w), AthionCore.topZ(plot.id, w));

        return (blocklocation.getBlockX() >= lowestX) && (blocklocation.getBlockX() <= highestX) && (blocklocation.getBlockZ() >= lowestZ) && (blocklocation.getBlockZ() <= highestZ);
    }

    public static List<Player> getPlayersInPlot(final World w, final String id) {
        final List<Player> playersInPlot = new ArrayList<Player>();

        for (final Player p : w.getPlayers()) {
            if (getPlotId(p).equals(id)) {
                playersInPlot.add(p);
            }
        }
        return playersInPlot;
    }

    public static void setSellSign(final World world, final AthionPlot plot) {
        removeSellSign(world, plot.id);

        if (plot.forsale || plot.auctionned) {
            final Location pillar = new Location(world, bottomX(plot.id, world) - 1, getMap(world).RoadHeight + 1, bottomZ(plot.id, world) - 1);

            Block bsign = pillar.clone().add(-1, 0, 0).getBlock();
            bsign.setType(Material.AIR);
            bsign.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, false);

            Sign sign = (Sign) bsign.getState();

            if (plot.forsale) {
                sign.setLine(0, AthionPlots.caption("SignForSale"));
                sign.setLine(1, AthionPlots.caption("SignPrice"));
                if ((plot.customprice % 1) == 0) {
                    sign.setLine(2, AthionPlots.caption("SignPriceColor") + Math.round(plot.customprice));
                } else {
                    sign.setLine(2, AthionPlots.caption("SignPriceColor") + plot.customprice);
                }
                sign.setLine(3, "/ap " + AthionPlots.caption("CommandBuy"));

                sign.update(true);
            }

            if (plot.auctionned) {
                if (plot.forsale) {
                    bsign = pillar.clone().add(-1, 0, 1).getBlock();
                    bsign.setType(Material.AIR);
                    bsign.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, false);

                    sign = (Sign) bsign.getState();
                }

                sign.setLine(0, "" + AthionPlots.caption("SignOnAuction"));
                if (plot.currentbidder.equals("")) {
                    sign.setLine(1, AthionPlots.caption("SignMinimumBid"));
                } else {
                    sign.setLine(1, AthionPlots.caption("SignCurrentBid"));
                }
                if ((plot.currentbid % 1) == 0) {
                    sign.setLine(2, AthionPlots.caption("SignCurrentBidColor") + Math.round(plot.currentbid));
                } else {
                    sign.setLine(2, AthionPlots.caption("SignCurrentBidColor") + plot.currentbid);
                }
                sign.setLine(3, "/ap " + AthionPlots.caption("CommandBid") + " <x>");

                sign.update(true);
            }
        }
    }

    public static void removeOwnerSign(final World world, final String id) {
        final Location bottom = getPlotBottomLoc(world, id);

        final Location pillar = new Location(world, bottom.getX() - 1, getMap(world).RoadHeight + 1, bottom.getZ() - 1);

        final Block bsign = pillar.add(0, 0, -1).getBlock();
        final Block redlight = pillar.add(0, -1, 0).getBlock();
        bsign.setType(Material.AIR);
        redlight.setType(Material.WOOD);
    }

    public static void removeSellSign(final World world, final String id) {
        final Location bottom = getPlotBottomLoc(world, id);

        final Location pillar = new Location(world, bottom.getX() - 1, getMap(world).RoadHeight + 1, bottom.getZ() - 1);

        Block bsign = pillar.clone().add(-1, 0, 0).getBlock();
        bsign.setType(Material.AIR);

        bsign = pillar.clone().add(-1, 0, 1).getBlock();
        bsign.setType(Material.AIR);
    }

    public static boolean isPlotAvailable(final String id, final World world) {
        return isPlotAvailable(id, world.getName().toLowerCase());
    }

    public static boolean isPlotAvailable(final String id, final Player p) {
        return isPlotAvailable(id, p.getWorld().getName().toLowerCase());
    }

    public static boolean isPlotAvailable(final String id, final String world) {
        if (isPlotWorld(world)) {
            return !getPlots(world).containsKey(id);
        } else {
            return false;
        }
    }

    public static String getPlotId(final Player player) {
        return getPlotID(player.getLocation());
    }

    public static AthionMaps getMap(final World w) {
        if (w == null) {
            return null;
        } else {
            final String worldname = w.getName().toLowerCase();

            if (AthionPlots.AthionMaps.containsKey(worldname)) {
                return AthionPlots.AthionMaps.get(worldname);
            } else {
                return null;
            }
        }
    }

    public static AthionMaps getMap(final String name) {
        final String worldname = name.toLowerCase();

        if (AthionPlots.AthionMaps.containsKey(worldname)) {
            return AthionPlots.AthionMaps.get(worldname);
        } else {
            return null;
        }
    }

    public static AthionMaps getMap(final Location l) {
        if (l == null) {
            return null;
        } else {
            final String worldname = l.getWorld().getName().toLowerCase();

            if (AthionPlots.AthionMaps.containsKey(worldname)) {
                return AthionPlots.AthionMaps.get(worldname);
            } else {
                return null;
            }
        }
    }

    public static AthionMaps getMap(final Player p) {
        if (p == null) {
            return null;
        } else {
            final String worldname = p.getWorld().getName().toLowerCase();

            if (AthionPlots.AthionMaps.containsKey(worldname)) {
                return AthionPlots.AthionMaps.get(worldname);
            } else {
                return null;
            }
        }
    }

    public static AthionMaps getMap(final Block b) {
        if (b == null) {
            return null;
        } else {
            final String worldname = b.getWorld().getName().toLowerCase();

            if (AthionPlots.AthionMaps.containsKey(worldname)) {
                return AthionPlots.AthionMaps.get(worldname);
            } else {
                return null;
            }
        }
    }

    public static int getIdX(final String id) {
        return Integer.parseInt(id.substring(0, id.indexOf(";")));
    }

    public static int getIdZ(final String id) {
        return Integer.parseInt(id.substring(id.indexOf(";") + 1));
    }

    public static Location getPlotBottomLoc(final World world, final String id) {
        final int px = getIdX(id);
        final int pz = getIdZ(id);

        final AthionMaps pmi = getMap(world);

        final int x = (px * (pmi.PlotSize + pmi.PathWidth)) - (pmi.PlotSize) - ((int) Math.floor(pmi.PathWidth / 2));
        final int z = (pz * (pmi.PlotSize + pmi.PathWidth)) - (pmi.PlotSize) - ((int) Math.floor(pmi.PathWidth / 2));

        return new Location(world, x, 1, z);
    }

    public static Location getPlotTopLoc(final World world, final String id) {
        final int px = getIdX(id);
        final int pz = getIdZ(id);

        final AthionMaps pmi = getMap(world);

        final int x = (px * (pmi.PlotSize + pmi.PathWidth)) - ((int) Math.floor(pmi.PathWidth / 2)) - 1;
        final int z = (pz * (pmi.PlotSize + pmi.PathWidth)) - ((int) Math.floor(pmi.PathWidth / 2)) - 1;

        return new Location(world, x, 255, z);
    }

    public static void setBiome(final World w, final String id, final AthionPlot plot, final Biome b) {
        final int bottomX = AthionCore.bottomX(plot.id, w) - 1;
        final int topX = AthionCore.topX(plot.id, w) + 1;
        final int bottomZ = AthionCore.bottomZ(plot.id, w) - 1;
        final int topZ = AthionCore.topZ(plot.id, w) + 1;

        for (int x = bottomX; x <= topX; x++) {
            for (int z = bottomZ; z <= topZ; z++) {
                w.getBlockAt(x, 0, z).setBiome(b);
            }
        }

        plot.biome = b;

        refreshPlotChunks(w, plot);

        AthionSQL.updatePlot(getIdX(id), getIdZ(id), plot.world, "biome", b.name());
    }

    public static void refreshPlotChunks(final World w, final AthionPlot plot) {
        final int bottomX = AthionCore.bottomX(plot.id, w);
        final int topX = AthionCore.topX(plot.id, w);
        final int bottomZ = AthionCore.bottomZ(plot.id, w);
        final int topZ = AthionCore.topZ(plot.id, w);

        final int minChunkX = (int) Math.floor((double) bottomX / 16);
        final int maxChunkX = (int) Math.floor((double) topX / 16);
        final int minChunkZ = (int) Math.floor((double) bottomZ / 16);
        final int maxChunkZ = (int) Math.floor((double) topZ / 16);

        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                w.refreshChunk(x, z);
            }
        }
    }

    public static World getFirstWorld() {
        if (AthionPlots.AthionMaps != null) {
            if (AthionPlots.AthionMaps.keySet() != null) {
                if (AthionPlots.AthionMaps.keySet().toArray().length > 0) {
                    return Bukkit.getWorld((String) AthionPlots.AthionMaps.keySet().toArray()[0]);
                }
            }
        }
        return null;
    }

    public static World getFirstWorld(final UUID uuid) {
        if (AthionPlots.AthionMaps != null) {
            if (AthionPlots.AthionMaps.keySet() != null) {
                if (AthionPlots.AthionMaps.keySet().toArray().length > 0) {
                    for (final String mapkey : AthionPlots.AthionMaps.keySet()) {
                        for (final String id : AthionPlots.AthionMaps.get(mapkey).plots.keySet()) {
                            if (AthionPlots.AthionMaps.get(mapkey).plots.get(id).ownerId.equals(uuid)) {
                                return Bukkit.getWorld(mapkey);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static AthionPlot getFirstPlot(final UUID uuid) {
        if (AthionPlots.AthionMaps != null) {
            if (AthionPlots.AthionMaps.keySet() != null) {
                if (AthionPlots.AthionMaps.keySet().toArray().length > 0) {
                    for (final String mapkey : AthionPlots.AthionMaps.keySet()) {
                        for (final String id : AthionPlots.AthionMaps.get(mapkey).plots.keySet()) {
                            if (AthionPlots.AthionMaps.get(mapkey).plots.get(id).ownerId.equals(uuid)) {
                                return AthionPlots.AthionMaps.get(mapkey).plots.get(id);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Location getPlotHome(final World w, final AthionPlot plot) {
        final AthionMaps pmi = getMap(w);

        if (pmi != null) {
            final Location loc = new Location(w, bottomX(plot.id, w) + ((topX(plot.id, w) - AthionCore.bottomX(plot.id, w)) / 2), pmi.RoadHeight + 2, bottomZ(plot.id, w) - 2);
            final int y = w.getHighestBlockYAt(loc);
            if ((y > 1) || (y < 255)) {
                loc.setY(y);
            }
            return loc;

        } else {
            return w.getSpawnLocation();
        }
    }

    public static boolean isValidId(final String id) {
        final String[] coords = id.split(";");

        if (coords.length != 2) {
            return false;
        } else {
            try {
                Integer.parseInt(coords[0]);
                Integer.parseInt(coords[1]);
                return true;
            } catch (final Exception e) {
                return false;
            }
        }
    }

    public static void adjustLinkedPlots(final String id, final World world) {
        final AthionMaps pmi = getMap(world);

        if (pmi != null) {
            final HashMap<String, AthionPlot> plots = pmi.plots;

            final int x = getIdX(id);
            final int z = getIdZ(id);

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
                    fillroad(p01, p11, world);
                }

                if ((p10 != null) && p10.owner.equalsIgnoreCase(p11.owner)) {
                    fillroad(p10, p11, world);
                }

                if ((p12 != null) && p12.owner.equalsIgnoreCase(p11.owner)) {
                    fillroad(p12, p11, world);
                }

                if ((p21 != null) && p21.owner.equalsIgnoreCase(p11.owner)) {
                    fillroad(p21, p11, world);
                }

                if ((p00 != null) && (p10 != null) && (p01 != null) && p00.owner.equalsIgnoreCase(p11.owner) && p11.owner.equalsIgnoreCase(p10.owner) && p10.owner.equalsIgnoreCase(p01.owner)) {
                    fillmiddleroad(p00, p11, world);
                }

                if ((p10 != null) && (p20 != null) && (p21 != null) && p10.owner.equalsIgnoreCase(p11.owner) && p11.owner.equalsIgnoreCase(p20.owner) && p20.owner.equalsIgnoreCase(p21.owner)) {
                    fillmiddleroad(p20, p11, world);
                }

                if ((p01 != null) && (p02 != null) && (p12 != null) && p01.owner.equalsIgnoreCase(p11.owner) && p11.owner.equalsIgnoreCase(p02.owner) && p02.owner.equalsIgnoreCase(p12.owner)) {
                    fillmiddleroad(p02, p11, world);
                }

                if ((p12 != null) && (p21 != null) && (p22 != null) && p12.owner.equalsIgnoreCase(p11.owner) && p11.owner.equalsIgnoreCase(p21.owner) && p21.owner.equalsIgnoreCase(p22.owner)) {
                    fillmiddleroad(p22, p11, world);
                }

            }
        }
    }

    public static void resetroad(final AthionPlot plot1, final AthionPlot plot2, final World w) {
        final Location bottomPlot1 = getPlotBottomLoc(w, plot1.id);
        final Location topPlot1 = getPlotTopLoc(w, plot1.id);
        final Location bottomPlot2 = getPlotBottomLoc(w, plot2.id);
        final Location topPlot2 = getPlotTopLoc(w, plot2.id);

        int minX;
        int maxX;
        int minZ;
        int maxZ;
        boolean isWallX;

        final AthionMaps pmi = getMap(w);
        final int h = pmi.RoadHeight;
        final int wallId = pmi.WallBlockId;
        final byte wallValue = pmi.WallBlockValue;
        final byte fillValue = pmi.PlotFloorBlockValue;
        final int roadId = pmi.RoadMainBlockId;
        if (bottomPlot1.getBlockX() == bottomPlot2.getBlockX()) {
            minX = bottomPlot1.getBlockX();
            maxX = topPlot1.getBlockX();

            minZ = Math.min(bottomPlot1.getBlockZ(), bottomPlot2.getBlockZ()) + pmi.PlotSize;
            maxZ = Math.max(topPlot1.getBlockZ(), topPlot2.getBlockZ()) - pmi.PlotSize;
        } else {
            minZ = bottomPlot1.getBlockZ();
            maxZ = topPlot1.getBlockZ();

            minX = Math.min(bottomPlot1.getBlockX(), bottomPlot2.getBlockX()) + pmi.PlotSize;
            maxX = Math.max(topPlot1.getBlockX(), topPlot2.getBlockX()) - pmi.PlotSize;
        }

        isWallX = (maxX - minX) > (maxZ - minZ);

        if (isWallX) {
            minX--;
            maxX++;
        } else {
            minZ--;
            maxZ++;
        }

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = h; y < w.getMaxHeight(); y++) {

                    if (y != 0) {

                        if (y == h) {
                            w.getBlockAt(x, y, z).setTypeIdAndData(roadId, fillValue, true);
                        }

                        if (y == (h + 1)) {

                            if (isWallX && ((x == minX) || (x == maxX))) {
                                w.getBlockAt(x, y, z).setTypeIdAndData(wallId, wallValue, true);
                            } else if (!isWallX && ((z == minZ) || (z == maxZ))) {
                                w.getBlockAt(x, y, z).setTypeIdAndData(wallId, wallValue, true);
                            } else {
                                w.getBlockAt(x, y, z).setTypeId(wallId);
                            }

                            for (int z2 = (minZ + 1); z2 < maxZ; z2++) {
                                //Bukkit.broadcastMessage("Z: " + z);
                                w.getBlockAt(x, y, z2).setType(Material.AIR);
                            }

                            /*for (int x2 = (minX + 1); x2 < maxX; x2++)
                            {
                            	//Bukkit.broadcastMessage("Z: " + z);
                            	w.getBlockAt(x2, y, z).setType(Material.AIR);
                            }*/

                        }

                    }
                }
            }
        }
    }

    public static void fillroad(final AthionPlot plot1, final AthionPlot plot2, final World w) {
        final Location bottomPlot1 = getPlotBottomLoc(w, plot1.id);
        final Location topPlot1 = getPlotTopLoc(w, plot1.id);
        final Location bottomPlot2 = getPlotBottomLoc(w, plot2.id);
        final Location topPlot2 = getPlotTopLoc(w, plot2.id);

        int minX;
        int maxX;
        int minZ;
        int maxZ;
        boolean isWallX;

        final AthionMaps pmi = getMap(w);
        final int h = pmi.RoadHeight;
        final int wallId = pmi.WallBlockId;
        final byte wallValue = pmi.WallBlockValue;
        final int fillId = pmi.PlotFloorBlockId;
        final byte fillValue = pmi.PlotFloorBlockValue;

        if (bottomPlot1.getBlockX() == bottomPlot2.getBlockX()) {
            minX = bottomPlot1.getBlockX();
            maxX = topPlot1.getBlockX();

            minZ = Math.min(bottomPlot1.getBlockZ(), bottomPlot2.getBlockZ()) + pmi.PlotSize;
            maxZ = Math.max(topPlot1.getBlockZ(), topPlot2.getBlockZ()) - pmi.PlotSize;
        } else {
            minZ = bottomPlot1.getBlockZ();
            maxZ = topPlot1.getBlockZ();

            minX = Math.min(bottomPlot1.getBlockX(), bottomPlot2.getBlockX()) + pmi.PlotSize;
            maxX = Math.max(topPlot1.getBlockX(), topPlot2.getBlockX()) - pmi.PlotSize;
        }

        isWallX = (maxX - minX) > (maxZ - minZ);

        if (isWallX) {
            minX--;
            maxX++;
        } else {
            minZ--;
            maxZ++;
        }

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = h; y < w.getMaxHeight(); y++) {
                    if (y >= (h + 2)) {
                        w.getBlockAt(x, y, z).setType(Material.AIR);
                    } else if (y == (h + 1)) {
                        if (isWallX && ((x == minX) || (x == maxX))) {
                            w.getBlockAt(x, y, z).setTypeIdAndData(wallId, wallValue, true);
                        } else if (!isWallX && ((z == minZ) || (z == maxZ))) {
                            w.getBlockAt(x, y, z).setTypeIdAndData(wallId, wallValue, true);
                        } else {
                            w.getBlockAt(x, y, z).setType(Material.AIR);
                        }
                    } else {
                        w.getBlockAt(x, y, z).setTypeIdAndData(fillId, fillValue, true);
                    }
                }
            }
        }
    }

    public static void fillmiddleroad(final AthionPlot plot1, final AthionPlot plot2, final World w) {
        final Location bottomPlot1 = getPlotBottomLoc(w, plot1.id);
        final Location topPlot1 = getPlotTopLoc(w, plot1.id);
        final Location bottomPlot2 = getPlotBottomLoc(w, plot2.id);
        final Location topPlot2 = getPlotTopLoc(w, plot2.id);

        int minX;
        int maxX;
        int minZ;
        int maxZ;

        final AthionMaps pmi = getMap(w);
        final int h = pmi.RoadHeight;
        final int fillId = pmi.PlotFloorBlockId;

        minX = Math.min(topPlot1.getBlockX(), topPlot2.getBlockX());
        maxX = Math.max(bottomPlot1.getBlockX(), bottomPlot2.getBlockX());

        minZ = Math.min(topPlot1.getBlockZ(), topPlot2.getBlockZ());
        maxZ = Math.max(bottomPlot1.getBlockZ(), bottomPlot2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = h; y < w.getMaxHeight(); y++) {
                    if (y >= (h + 1)) {
                        w.getBlockAt(x, y, z).setType(Material.AIR);
                    } else {
                        w.getBlockAt(x, y, z).setTypeId(fillId);
                    }
                }
            }
        }
    }

    public static boolean isEconomyEnabled(final World w) {
        final AthionMaps pmi = getMap(w);

        if (pmi == null) {
            return false;
        } else {
            return pmi.UseEconomy && AthionPlots.globalUseEconomy && (AthionPlots.economy != null);
        }
    }

    public static boolean isEconomyEnabled(final String name) {
        final AthionMaps pmi = getMap(name);

        if (pmi == null) {
            return false;
        } else {
            return pmi.UseEconomy && AthionPlots.globalUseEconomy;
        }
    }

    public static boolean isEconomyEnabled(final Player p) {
        if (AthionPlots.economy == null) {
            return false;
        }

        final AthionMaps pmi = getMap(p);

        if (pmi == null) {
            return false;
        } else {
            return pmi.UseEconomy && AthionPlots.globalUseEconomy;
        }
    }

    public static boolean isEconomyEnabled(final Block b) {
        final AthionMaps pmi = getMap(b);

        if (pmi == null) {
            return false;
        } else {
            return pmi.UseEconomy && AthionPlots.globalUseEconomy;
        }
    }

    public static HashMap<String, AthionPlot> getPlots(final World w) {
        final AthionMaps pmi = getMap(w);

        if (pmi == null) {
            return null;
        } else {
            return pmi.plots;
        }
    }

    public static HashMap<String, AthionPlot> getPlots(final String name) {
        final AthionMaps pmi = getMap(name);

        if (pmi == null) {
            return null;
        } else {
            return pmi.plots;
        }
    }

    public static HashMap<String, AthionPlot> getPlots(final Player p) {
        final AthionMaps pmi = getMap(p);

        if (pmi == null) {
            return null;
        } else {
            return pmi.plots;
        }
    }

    public static HashMap<String, AthionPlot> getPlots(final Block b) {
        final AthionMaps pmi = getMap(b);

        if (pmi == null) {
            return null;
        } else {
            return pmi.plots;
        }
    }

    public static HashMap<String, AthionPlot> getPlots(final Location l) {
        final AthionMaps pmi = getMap(l);

        if (pmi == null) {
            return null;
        } else {
            return pmi.plots;
        }
    }

    public static AthionPlot getPlotById(final World w, final String id) {
        final HashMap<String, AthionPlot> plots = getPlots(w);

        if (plots == null) {
            return null;
        } else {
            return plots.get(id);
        }
    }

    public static AthionPlot getPlotById(final String name, final String id) {
        final HashMap<String, AthionPlot> plots = getPlots(name);

        if (plots == null) {
            return null;
        } else {
            return plots.get(id);
        }
    }

    public static AthionPlot getPlotById(final Player p, final String id) {
        final HashMap<String, AthionPlot> plots = getPlots(p);

        if (plots == null) {
            return null;
        } else {
            return plots.get(id);
        }
    }

    public static int getNbOwnedPlot(final Player p) {
        return getNbOwnedPlot(p.getUniqueId(), p.getWorld());
    }

    public static int getNbOwnedPlot(final Player p, final World w) {
        return getNbOwnedPlot(p.getUniqueId(), w);
    }

    public static int getNbOwnedPlot(final UUID uuid, final World w) {
        int nbfound = 0;
        if (AthionCore.getPlots(w) != null) {
            for (final AthionPlot plot : AthionCore.getPlots(w).values()) {
                if ((plot.ownerId != null) && plot.ownerId.equals(uuid)) {
                    nbfound++;
                }
            }
        }
        return nbfound;
    }

    public static AthionPlot getPlotById(final Player p) {
        final HashMap<String, AthionPlot> plots = getPlots(p);
        final String plotid = getPlotID(p.getLocation());

        if ((plots == null) || (plotid == "")) {
            return null;
        } else {
            return plots.get(plotid);
        }
    }

    public static AthionPlot getPlotById(final Location l) {
        final HashMap<String, AthionPlot> plots = getPlots(l);
        final String plotid = getPlotID(l);

        if ((plots == null) || (plotid == "")) {
            return null;
        } else {
            return plots.get(plotid);
        }
    }

    public static AthionPlot getPlotById(final Block b, final String id) {
        final HashMap<String, AthionPlot> plots = getPlots(b);

        if (plots == null) {
            return null;
        } else {
            return plots.get(id);
        }
    }

    public static AthionPlot getPlotById(final Block b) {
        final HashMap<String, AthionPlot> plots = getPlots(b);
        final String plotid = getPlotID(b.getLocation());

        if ((plots == null) || (plotid == "")) {
            return null;
        } else {
            return plots.get(plotid);
        }
    }

    public static int bottomX(final String id, final World w) {
        return getPlotBottomLoc(w, id).getBlockX();
    }

    public static int bottomZ(final String id, final World w) {
        return getPlotBottomLoc(w, id).getBlockZ();
    }

    public static int topX(final String id, final World w) {
        return getPlotTopLoc(w, id).getBlockX();
    }

    public static int topZ(final String id, final World w) {
        return getPlotTopLoc(w, id).getBlockZ();
    }

    public static boolean isPlotWorld(final World w) {
        if (w == null) {
            return false;
        } else {
            return AthionPlots.AthionMaps.containsKey(w.getName().toLowerCase());
        }
    }

    public static boolean isPlotWorld(final String name) {
        return AthionPlots.AthionMaps.containsKey(name.toLowerCase());
    }

    public static boolean isPlotWorld(final Location l) {
        if (l == null) {
            return false;
        } else {
            return AthionPlots.AthionMaps.containsKey(l.getWorld().getName().toLowerCase());
        }
    }

    public static boolean isPlotWorld(final Player p) {
        if (p == null) {
            return false;
        } else {
            return AthionPlots.AthionMaps.containsKey(p.getWorld().getName().toLowerCase());
        }
    }

    public static boolean isPlotWorld(final Block b) {
        if (b == null) {
            return false;
        } else {
            return AthionPlots.AthionMaps.containsKey(b.getWorld().getName().toLowerCase());
        }
    }

    public static boolean isPlotWorld(final BlockState b) {
        if (b == null) {
            return false;
        } else {
            return AthionPlots.AthionMaps.containsKey(b.getWorld().getName().toLowerCase());
        }
    }

    public static void UpdatePlayerNameFromId(final UUID uuid, final String name) {
        AthionSQL.updatePlotsNewUUID(uuid, name);

        Bukkit.getServer().getScheduler().runTaskAsynchronously(AthionPlots.self, new Runnable() {
            @Override
            public void run() {
                for (final AthionMaps pmi : AthionPlots.AthionMaps.values()) {
                    for (final AthionPlot plot : pmi.plots.values()) {

                        //Owner
                        if ((plot.ownerId != null) && plot.ownerId.equals(uuid)) {
                            plot.owner = name;
                        }

                        //Bidder
                        if ((plot.currentbidderId != null) && plot.currentbidderId.equals(uuid)) {
                            plot.currentbidder = name;
                        }

                        //Allowed
                        plot.allowed.replace(uuid, name);

                        //Denied
                        plot.denied.replace(uuid, name);

                        //Comments
                        for (final String[] comment : plot.comments) {
                            if ((comment.length > 2) && (comment[2] != null) && comment[2].equalsIgnoreCase(uuid.toString())) {
                                comment[0] = name;
                            }
                        }
                    }
                }
            }
        });
    }

    static void setBlock(final short[][] result, final int x, final int y, final int z, final short blkid) {
        if (result[y >> 4] == null) {
            result[y >> 4] = new short[4096];
        }
        result[y >> 4][((y & 0xf) << 8) | (z << 4) | x] = blkid;
    }

}
