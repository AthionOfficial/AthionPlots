package net.athion.athionplots.Core;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class AthionRoadPopulator extends BlockPopulator {

    private final double plotsize;
    private final double pathsize;

    private final byte wall;
    private final short wallid;
    private final byte floor1;
    private final short floor1id;
    private final byte floor2;
    private final short floor2id;

    private final byte pillarh1;
    private final short pillarh1id;
    private final byte pillarh2;
    private final short pillarh2id;

    private final int roadheight;

    public AthionRoadPopulator() {
        plotsize = 32;
        pathsize = 7;
        wall = 0;
        wallid = 44;
        floor2 = 2;
        floor2id = 5;
        floor1 = 0;
        floor1id = 5;

        pillarh1 = 4;
        pillarh1id = 17;

        pillarh2 = 8;
        pillarh2id = 17;

        roadheight = 64;
    }

    public AthionRoadPopulator(final AthionInfo ami) {
        plotsize = ami.PlotSize;
        pathsize = ami.PathWidth;
        wall = ami.WallBlockValue;
        wallid = ami.WallBlockId;
        floor1 = ami.RoadMainBlockValue;
        floor1id = ami.RoadMainBlockId;
        floor2 = ami.RoadStripeBlockValue;
        floor2id = ami.RoadStripeBlockId;
        roadheight = ami.RoadHeight;

        pillarh1 = ami.RoadMainBlockValue;
        pillarh1id = ami.RoadMainBlockId;

        pillarh2 = ami.RoadMainBlockValue;
        pillarh2id = ami.RoadMainBlockId;
    }

    @Override
    public void populate(final World w, final Random rand, final Chunk chunk) {
        final int cx = chunk.getX();
        final int cz = chunk.getZ();

        final int xx = cx << 4;
        final int zz = cz << 4;

        final double size = plotsize + pathsize;
        int valx;
        int valz;

        double n1;
        double n2;
        double n3;
        int mod2 = 0;
        final int mod1 = 1;

        if ((pathsize % 2) == 1) {
            n1 = Math.ceil((pathsize) / 2) - 2;
            n2 = Math.ceil((pathsize) / 2) - 1;
            n3 = Math.ceil((pathsize) / 2);
        } else {
            n1 = Math.floor((pathsize) / 2) - 2;
            n2 = Math.floor((pathsize) / 2) - 1;
            n3 = Math.floor((pathsize) / 2);
        }

        if ((pathsize % 2) == 1) {
            mod2 = -1;
        }

        for (int x = 0; x < 16; x++) {
            valx = ((cx * 16) + x);

            for (int z = 0; z < 16; z++) {
                valz = ((cz * 16) + z);

                final int y = roadheight;

                if (((((valx - n3) + mod1) % size) == 0) || (((valx + n3 + mod2) % size) == 0)) //middle+3
                {
                    boolean found = false;
                    for (double i = n2; i >= 0; i--) {
                        if (((((valz - i) + mod1) % size) == 0) || (((valz + i + mod2) % size) == 0)) {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        //setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
                        setBlock(w, x + xx, y, z + zz, floor1, floor1id);
                        //setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
                        //setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
                    } else {
                        setBlock(w, x + xx, y, z + zz, pillarh2, pillarh2id);
                        setBlock(w, x + xx, y + 1, z + zz, wall, wallid);
                    }
                } else {
                    boolean found5 = false;
                    for (double i = n2; i >= 0; i--) {
                        if (((((valx - i) + mod1) % size) == 0) || (((valx + i + mod2) % size) == 0)) {
                            found5 = true;
                            break;
                        }
                    }

                    if (!found5) {
                        if (((((valz - n3) + mod1) % size) == 0) || (((valz + n3 + mod2) % size) == 0)) {
                            setBlock(w, x + xx, y, z + zz, pillarh1, pillarh1id);
                            setBlock(w, x + xx, y + 1, z + zz, wall, wallid);
                        }
                    }

                    if (((((valx - n2) + mod1) % size) == 0) || (((valx + n2 + mod2) % size) == 0)) //middle+2
                    {
                        if (((((valz - n3) + mod1) % size) == 0) || (((valz + n3 + mod2) % size) == 0) || ((((valz - n2) + mod1) % size) == 0) || (((valz + n2 + mod2) % size) == 0)) {
                            //setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
                            setBlock(w, x + xx, y, z + zz, floor1, floor1id);
                            //setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
                            //setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
                        } else {
                            //setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
                            setBlock(w, x + xx, y, z + zz, floor2, floor2id);
                            //setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
                            //setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
                        }
                    } else if (((((valx - n1) + mod1) % size) == 0) || (((valx + n1 + mod2) % size) == 0)) //middle+2
                    {
                        if (((((valz - n2) + mod1) % size) == 0) || (((valz + n2 + mod2) % size) == 0) || ((((valz - n1) + mod1) % size) == 0) || (((valz + n1 + mod2) % size) == 0)) {
                            //setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
                            setBlock(w, x + xx, y, z + zz, floor2, floor2id);
                            //setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
                            //setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
                        } else {
                            //setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
                            setBlock(w, x + xx, y, z + zz, floor1, floor1id);
                            //setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
                            //setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
                        }
                    } else {
                        boolean found = false;
                        for (double i = n1; i >= 0; i--) {
                            if (((((valz - i) + mod1) % size) == 0) || (((valz + i + mod2) % size) == 0)) {
                                found = true;
                                break;
                            }
                        }

                        if (found) {
                            //setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
                            setBlock(w, x + xx, y, z + zz, floor1, floor1id);
                            //setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
                            //setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
                        } else {
                            if (((((valz - n2) + mod1) % size) == 0) || (((valz + n2 + mod2) % size) == 0)) {
                                //setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
                                setBlock(w, x + xx, y, z + zz, floor2, floor2id);
                                //setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
                                //setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
                            } else {
                                boolean found2 = false;
                                for (double i = n1; i >= 0; i--) {
                                    if (((((valz - i) + mod1) % size) == 0) || (((valz + i + mod2) % size) == 0)) {
                                        found2 = true;
                                        break;
                                    }
                                }

                                if (found2) {
                                    //setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
                                    setBlock(w, x + xx, y, z + zz, floor1, floor1id);
                                    //setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
                                    //setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
                                } else {
                                    boolean found3 = false;
                                    for (double i = n3; i >= 0; i--) {
                                        if (((((valx - i) + mod1) % size) == 0) || (((valx + i + mod2) % size) == 0)) {
                                            found3 = true;
                                            break;
                                        }
                                    }

                                    if (found3) {
                                        //setBlock(w, x + xx, y - 1, z + zz, pillar, pillarid);
                                        setBlock(w, x + xx, y, z + zz, floor1, floor1id);
                                        //setBlock(w, x + xx, y + 1, z + zz, (byte) 0, (short) Material.AIR.getId());
                                        //setBlock(w, x + xx, y + 2, z + zz, (byte) 0, (short) Material.AIR.getId());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void setBlock(final World w, final int x, final int y, final int z, final byte val, final short id) {
        if (val != 0) {
            w.getBlockAt(x, y, z).setTypeIdAndData(id, val, false);
        } else {
            w.getBlockAt(x, y, z).setTypeId(id);
        }
    }

}
