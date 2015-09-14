package net.athion.athionplots.Core;

public class AthionSchematic {

    private final byte[] blocks;
    private final byte[] data;
    private final short width;
    private final short lenght;
    private final short height;

    public AthionSchematic(final byte[] blocks, final byte[] data, final short width, final short lenght, final short height) {
        this.blocks = blocks;
        this.data = data;
        this.width = width;
        this.lenght = lenght;
        this.height = height;
    }

    /**
     * @return the blocks
     */
    public byte[] getBlocks() {
        return blocks;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @return the width
     */
    public short getWidth() {
        return width;
    }

    /**
     * @return the lenght
     */
    public short getLenght() {
        return lenght;
    }

    /**
     * @return the height
     */
    public short getHeight() {
        return height;
    }

}
