package com.reine.block;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.Collection;

public class Block {
    private static final Int2ObjectMap<Block> ID_BLOCK_MAP = new Int2ObjectArrayMap<>();

    public static final Block BEDROCK = new Block(1, "bedrock.png");
    public static final Block DIRT = new Block(2, "dirt.png");
    public static final Block GLASS = new Block(3, "glass.png");
    public static final Block ICE = new Block(4, "ice.png");
    public static final Block MAGMA = new Block(5, "magma.png");
    public static final Block CLAY = new Block(6, "clay.png");
    public static final Block BOOKSHELF = new Block(7, "bookshelf.png");
    public static final Block SEA_LANTERN = new Block(8, "sea_lantern.png");

    private final int id;
    private final String texture;

    public Block(int id, String texture) {
        this.id = id;
        this.texture = texture;

        ID_BLOCK_MAP.put(id, this);
    }

    public int getId() {
        return id;
    }

    public String getTexture() {
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
