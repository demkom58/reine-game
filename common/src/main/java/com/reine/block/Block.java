package com.reine.block;

import com.reine.util.WorldSide;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.Collection;

public class Block {
    private static final Int2ObjectMap<Block> ID_BLOCK_MAP = new Int2ObjectArrayMap<>();

    public static final Block AIR = new Block(0, true, null);
    public static final Block BEDROCK = new Block(1, false, "bedrock.png");
    public static final Block DIRT = new Block(2, false, "dirt.png");
    public static final Block GLASS = new Block(3, true, "glass.png");
    public static final Block ICE = new Block(4, true, "ice.png");
    public static final Block MAGMA = new Block(5, false, "magma.png");
    public static final Block CLAY = new Block(6, false, "clay.png");
    public static final Block BOOKSHELF = new Block(7, false, "bookshelf.png");
    public static final Block SEA_LANTERN = new Block(8, false, "sea_lantern.png");

    private final int id;
    private final boolean transparent;
    private final String texture;

    public Block(int id, boolean transparent, String texture) {
        this.id = id;
        this.transparent = transparent;
        this.texture = texture;

        ID_BLOCK_MAP.put(id, this);
    }

    public int getId() {
        return id;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public String getTexture(WorldSide side) {
        return texture;
    }

    public static Block byId(int id) {
        return ID_BLOCK_MAP.get(id);
    }

    public static Collection<Block> values() {
        return ID_BLOCK_MAP.values();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return id == block.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
