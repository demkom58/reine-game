package com.reine.block;

import com.reine.util.WorldSide;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.Collection;

public class Block {
    private static final Int2ObjectMap<Block> ID_BLOCK_MAP = new Int2ObjectArrayMap<>();

    public static final Block AIR = new Block(0, null, null);
    public static final Block BEDROCK = new Block(1, BlockLayer.SOLID, "bedrock.png");
    public static final Block DIRT = new Block(2, BlockLayer.SOLID, "dirt.png");
    public static final Block GRASS = new Block(3, BlockLayer.OPAQUE, "grass.png", false);
    public static final Block STONE = new Block(4, BlockLayer.SOLID, "stone.png");
    public static final Block COBBLESTONE = new Block(5, BlockLayer.SOLID, "cobblestone.png");
    public static final Block GLASS = new Block(6, BlockLayer.OPAQUE, "glass.png");
    public static final Block ICE = new Block(7, BlockLayer.TRANSPARENT, "ice.png");
    public static final Block MAGMA = new Block(8, BlockLayer.SOLID, "magma.png");
    public static final Block CLAY = new Block(9, BlockLayer.SOLID, "clay.png");
    public static final Block BOOKSHELF = new Block(10, BlockLayer.SOLID, "bookshelf.png");
    public static final Block SEA_LANTERN = new Block(11, BlockLayer.SOLID, "sea_lantern.png");
    public static final Block SAND = new Block(12, BlockLayer.SOLID, "sand.png");
    public static final Block WATER = new Block(13, BlockLayer.TRANSPARENT, "water.png");
    public static final Block LAVA = new Block(14, BlockLayer.TRANSPARENT, "lava.png");
    public static final Block GRAVEL = new Block(15, BlockLayer.SOLID, "gravel.png");
    public static final Block LEAVES = new Block(16, BlockLayer.OPAQUE, "leaves.png");
    public static final Block INVALID = new Block(17, BlockLayer.SOLID, "invalid.png");
    public static final Block DIORITE = new Block(18, BlockLayer.SOLID, "diorite.png");
    public static final Block OAK_WOOD = new Block(19, BlockLayer.SOLID, "oak_wood.png");
    public static final Block OBSIDIAN = new Block(20, BlockLayer.SOLID, "obsidian.png");
    public static final Block GRANITE = new Block(21, BlockLayer.SOLID, "granite.png");
    public static final Block ANDESITE = new Block(22, BlockLayer.SOLID, "andesite.png");
    public static final Block COAL_ORE = new Block(23, BlockLayer.SOLID, "coal_ore.png");
    public static final Block LAPIS_ORE = new Block(24, BlockLayer.SOLID, "lapis_ore.png");
    public static final Block REDSTONE_ORE = new Block(25, BlockLayer.SOLID, "redstone_ore.png");
    public static final Block GOLD_ORE = new Block(26, BlockLayer.SOLID, "gold_ore.png");
    public static final Block DIAMOND_ORE = new Block(27, BlockLayer.SOLID, "diamond_ore.png");
    public static final Block EMERALD_ORE = new Block(28, BlockLayer.SOLID, "emerald_ore.png");
    public static final Block BRICH_WOOD = new Block(29, BlockLayer.SOLID, "brich_wood.png");
    public static final Block IRON_ORE = new Block(30, BlockLayer.SOLID, "iron_ore.png");
    public static final Block MOSSY_COBBLESTONE = new Block(31, BlockLayer.SOLID, "mossy_cobblestone.png");
    public static final Block GRASS_BLOCK = new Block(32, BlockLayer.SOLID, "grass_block.png");
    public static final Block WHEAT_BLOCK = new Block(33, BlockLayer.OPAQUE, "wheat.png", false);
    public static final Block FARMLAND_BLOCK = new Block(34, BlockLayer.SOLID, "farmland.png");

    private final int id;
    private final BlockLayer layer;
    private final String texture;
    private final boolean fullBlock;

    public Block(int id, BlockLayer layer, String texture) {
        this(id, layer, texture, true);
    }

    public Block(int id, BlockLayer layer, String texture, boolean fullBlock) {
        this.id = id;
        this.layer = layer;
        this.texture = texture;
        this.fullBlock = fullBlock;

        ID_BLOCK_MAP.put(id, this);
    }

    public int getId() {
        return id;
    }

    public BlockLayer getLayer() {
        return layer;
    }

    public String getTexture(WorldSide side) {
        return texture;
    }

    public boolean isFullBlock() {
        return fullBlock;
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
