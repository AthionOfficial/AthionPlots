package net.athion.athionplots.Core;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

public class AthionGen extends ChunkGenerator {

    /*
     * Do not modify below unless you know what you're doing.
     * Any changes can alter the generation or propagation of plots.
     */

    // AthionGen Variables
    // Do Not Modify
    private final double APSIZE;
    private final double APPATHSIZE;
    private final short MAP_BOTTOM;
    private final short MAP_WALL;
    private final short PLOT_FLOOR;
    private final short MAP_FILL;
    private final short MAP_FLOOR_1;
    private final short MAP_FLOOR_2;
    private final int MAP_HEIGHT;
    
    /*
     * Default AthionGen Variables
     */
    public AthionGen() {
        MAP_BOTTOM = 7; // Bedrock Level
        MAP_WALL = 44; // The Block ID for the Walls
        PLOT_FLOOR = 2; // The Block ID for the Plot Floors
        MAP_FILL = 3; // The Block ID for the Map Filler (i.e. Dirt)
        MAP_FLOOR_1 = 5; // The Block ID for the Roads
        MAP_FLOOR_2 = 5; // The Block ID for the Roads (2)
        MAP_HEIGHT = 64; // The Default Map Height.
        APSIZE = 32; // AthionPlots Plot Size (Default: 32)
        APPATHSIZE = 7; // AthionPlots Path Size (Default: 7)
    }

    /*
     * Configuration AthionGen Variables
     * This will attempt to retrieve the configuration (config.yml) variables for the respective variable name.
     */
    public AthionGen(final AthionMaps pmi) {
        APSIZE = pmi.PlotSize;
        APPATHSIZE = pmi.PathWidth;
        MAP_BOTTOM = pmi.BottomBlockId;
        MAP_WALL = pmi.WallBlockId;
        PLOT_FLOOR = pmi.PlotFloorBlockId;
        MAP_FILL = pmi.PlotFillingBlockId;
        MAP_HEIGHT = pmi.RoadHeight;
        MAP_FLOOR_1 = pmi.RoadMainBlockId;
        MAP_FLOOR_2 = pmi.RoadStripeBlockId;
    }

    /*
     * AthionGen World Generator
     * Source: Zach-Bora @ PlotMe
     * Modified: by Travis506
     * Note: Very poorly done, and efficient method of Plot Generation.
     */
    @Override
    public short[][] generateExtBlockSections(final World world, final Random random, final int cx, final int cz, final BiomeGrid biomes) {
        // Generation Variables
        final int ymax = world.getMaxHeight();
        final short[][] result = new short[ymax / 16][];

        final double size = APSIZE + APPATHSIZE;
        int valx;
        int valz;

        double n1;
        double n2;
        double n3;
        int mod2 = 0;
        final int mod1 = 1;

        if ((APPATHSIZE % 2) == 1) {
            n1 = Math.ceil((APPATHSIZE) / 2) - 2;
            n2 = Math.ceil((APPATHSIZE) / 2) - 1;
            n3 = Math.ceil((APPATHSIZE) / 2);
        } else {
            n1 = Math.floor((APPATHSIZE) / 2) - 2;
            n2 = Math.floor((APPATHSIZE) / 2) - 1;
            n3 = Math.floor((APPATHSIZE) / 2);
        }

        if ((APPATHSIZE % 2) == 1) {
            mod2 = -1;
        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                biomes.setBiome(x, z, Biome.PLAINS);
            }
        }

        // Generation
        // Source: ZachBora @ PlotMe - Modified Version
        for (int x = 0; x < 16; x++) {
            valx = ((cx * 16) + x);
            for (int z = 0; z < 16; z++) {
                final int HEIGHT = MAP_HEIGHT + 2;
                valz = ((cz * 16) + z);
                for (int y = 0; y < HEIGHT; y++) {
                    // Bedrock Placement
                    if (y == 0) {
                        AthionCore.setBlock(result, x, y, z, MAP_BOTTOM);
                    } else if (y == MAP_HEIGHT) {
                        if (((((valx - n3) + mod1) % size) == 0) || (((valx + n3 + mod2) % size) == 0)) {
                            boolean FOUND = false;
                            for (double i = n2; i >= 0; i--) {
                                if (((((valz - i) + mod1) % size) == 0) || (((valz + i + mod2) % size) == 0)) {
                                    FOUND = true;
                                    break;
                                }
                            }
                            if (FOUND) {
                                // Sets the Map Floor
                                AthionCore.setBlock(result, x, y, z, MAP_FLOOR_1);
                            } else {
                                // Rest of the Map Fill
                                AthionCore.setBlock(result, x, y, z, MAP_FILL);
                            }
                        } else if (((((valx - n2) + mod1) % size) == 0) || (((valx + n2 + mod2) % size) == 0)) {
                            if (((((valz - n3) + mod1) % size) == 0) || (((valz + n3 + mod2) % size) == 0) || ((((valz - n2) + mod1) % size) == 0) || (((valz + n2 + mod2) % size) == 0)) {
                                AthionCore.setBlock(result, x, y, z, MAP_FLOOR_1);
                            } else {
                                AthionCore.setBlock(result, x, y, z, MAP_FLOOR_2);
                            }
                        } else if (((((valx - n1) + mod1) % size) == 0) || (((valx + n1 + mod2) % size) == 0)) {
                            if (((((valz - n2) + mod1) % size) == 0) || (((valz + n2 + mod2) % size) == 0) || ((((valz - n1) + mod1) % size) == 0) || (((valz + n1 + mod2) % size) == 0)) {
                                AthionCore.setBlock(result, x, y, z, MAP_FLOOR_2);
                            } else {
                                AthionCore.setBlock(result, x, y, z, MAP_FLOOR_1);
                            }
                        } else {
                            boolean FOUND = false;
                            for (double i = n1; i >= 0; i--) {
                                if (((((valz - i) + mod1) % size) == 0) || (((valz + i + mod2) % size) == 0)) {
                                    FOUND = true;
                                    break;
                                }
                            }
                            if (FOUND) {
                                AthionCore.setBlock(result, x, y, z, MAP_FLOOR_1);
                            } else {
                                if (((((valz - n2) + mod1) % size) == 0) || (((valz + n2 + mod2) % size) == 0)) {
                                    AthionCore.setBlock(result, x, y, z, MAP_FLOOR_2);
                                } else {
                                    boolean found2 = false;
                                    for (double i = n1; i >= 0; i--) {
                                        if (((((valz - i) + mod1) % size) == 0) || (((valz + i + mod2) % size) == 0)) {
                                            found2 = true;
                                            break;
                                        }
                                    }
                                    if (found2) {
                                        AthionCore.setBlock(result, x, y, z, MAP_FLOOR_1);
                                    } else {
                                        boolean found3 = false;
                                        for (double i = n3; i >= 0; i--) {
                                            if (((((valx - i) + mod1) % size) == 0) || (((valx + i + mod2) % size) == 0)) {
                                                found3 = true;
                                                break;
                                            }
                                        }
                                        if (found3) {
                                            AthionCore.setBlock(result, x, y, z, MAP_FLOOR_1);
                                        } else {
                                            AthionCore.setBlock(result, x, y, z, PLOT_FLOOR);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (y == (MAP_HEIGHT + 1)) {

                        if (((((valx - n3) + mod1) % size) == 0) || (((valx + n3 + mod2) % size) == 0)) // middle+3
                        {
                            boolean found = false;
                            for (double i = n2; i >= 0; i--) {
                                if (((((valz - i) + mod1) % size) == 0) || (((valz + i + mod2) % size) == 0)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (found) {} else {
                                AthionCore.setBlock(result, x, y, z, MAP_WALL);
                            }
                        } else {
                            boolean FOUND = false;
                            for (double i = n2; i >= 0; i--) {
                                if (((((valx - i) + mod1) % size) == 0) || (((valx + i + mod2) % size) == 0)) {
                                    FOUND = true;
                                    break;
                                }
                            }
                            if (!FOUND) {
                                if (((((valz - n3) + mod1) % size) == 0) || (((valz + n3 + mod2) % size) == 0)) {
                                    AthionCore.setBlock(result, x, y, z, MAP_WALL);
                                } else {}
                            }
                        }
                    } else {
                        AthionCore.setBlock(result, x, y, z, MAP_FILL);
                    }
                }
            }
        }
        for (int x = 0; x < 16; x++) {
            valx = ((cx * 16) + x);
            for (int z = 0; z < 16; z++) {
                final int height = MAP_HEIGHT + 2;
                valz = ((cz * 16) + z);
                for (int y = 0; y < height; y++) {

                    if (y == 0) {
                        AthionCore.setBlock(result, x, y, z, MAP_BOTTOM);
                    } else if (y == MAP_HEIGHT) {
                        if (((((valx - n3) + mod1) % size) == 0) || (((valx + n3 + mod2) % size) == 0)) {
                            boolean found = false;
                            for (double i = n2; i >= 0; i--) {
                                if (((((valz - i) + mod1) % size) == 0) || (((valz + i + mod2) % size) == 0)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (found) {
                                AthionCore.setBlock(result, x, y, z, MAP_FLOOR_1);
                            } else {
                                AthionCore.setBlock(result, x, y, z, MAP_FILL);
                            }

                        } else if (((((valx - n2) + mod1) % size) == 0) || (((valx + n2 + mod2) % size) == 0)) {
                            if (((((valz - n3) + mod1) % size) == 0) || (((valz + n3 + mod2) % size) == 0) || ((((valz - n2) + mod1) % size) == 0) || (((valz + n2 + mod2) % size) == 0)) {
                                AthionCore.setBlock(result, x, y, z, MAP_FLOOR_1);
                            } else {
                                AthionCore.setBlock(result, x, y, z, MAP_FLOOR_2);
                            }
                        } else if (((((valx - n1) + mod1) % size) == 0) || (((valx + n1 + mod2) % size) == 0)) {
                            if (((((valz - n2) + mod1) % size) == 0) || (((valz + n2 + mod2) % size) == 0) || ((((valz - n1) + mod1) % size) == 0) || (((valz + n1 + mod2) % size) == 0)) {
                                AthionCore.setBlock(result, x, y, z, MAP_FLOOR_2);
                            } else {
                                AthionCore.setBlock(result, x, y, z, MAP_FLOOR_1);
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
                                AthionCore.setBlock(result, x, y, z, MAP_FLOOR_1);
                            } else {
                                if (((((valz - n2) + mod1) % size) == 0) || (((valz + n2 + mod2) % size) == 0)) {
                                    AthionCore.setBlock(result, x, y, z, MAP_FLOOR_2);
                                } else {
                                    boolean found2 = false;
                                    for (double i = n1; i >= 0; i--) {
                                        if (((((valz - i) + mod1) % size) == 0) || (((valz + i + mod2) % size) == 0)) {
                                            found2 = true;
                                            break;
                                        }
                                    }
                                    if (found2) {
                                        AthionCore.setBlock(result, x, y, z, MAP_FLOOR_1);
                                    } else {
                                        boolean found3 = false;
                                        for (double i = n3; i >= 0; i--) {
                                            if (((((valx - i) + mod1) % size) == 0) || (((valx + i + mod2) % size) == 0)) {
                                                found3 = true;
                                                break;
                                            }
                                        }
                                        if (found3) {
                                            AthionCore.setBlock(result, x, y, z, MAP_FLOOR_1);
                                        } else {
                                            AthionCore.setBlock(result, x, y, z, PLOT_FLOOR);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (y == (MAP_HEIGHT + 1)) {

                        if (((((valx - n3) + mod1) % size) == 0) || (((valx + n3 + mod2) % size) == 0)) // middle+3
                        {
                            boolean found = false;
                            for (double i = n2; i >= 0; i--) {
                                if (((((valz - i) + mod1) % size) == 0) || (((valz + i + mod2) % size) == 0)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (found) {} else {
                                AthionCore.setBlock(result, x, y, z, MAP_WALL);
                            }
                        } else {
                            boolean found = false;
                            for (double i = n2; i >= 0; i--) {
                                if (((((valx - i) + mod1) % size) == 0) || (((valx + i + mod2) % size) == 0)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                if (((((valz - n3) + mod1) % size) == 0) || (((valz + n3 + mod2) % size) == 0)) {
                                    AthionCore.setBlock(result, x, y, z, MAP_WALL);
                                } else {}
                            }
                        }
                    } else {
                        AthionCore.setBlock(result, x, y, z, MAP_FILL);
                    }

                }

            }
        }

        return result;
    }

    /*
     * Sets the World Spawn Location
     * Modified: by Travis506
     */

    @Override
    public Location getFixedSpawnLocation(final World world, final Random random) {
        return new Location(world, 0, MAP_HEIGHT + 2, 0);
    }

}
