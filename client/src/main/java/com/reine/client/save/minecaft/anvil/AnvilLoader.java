package com.reine.client.save.minecaft.anvil;

import com.reine.block.Block;
import com.reine.world.chunk.Chunk;
import com.reine.world.chunk.EmptyChunk;
import com.reine.world.chunk.IChunk;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jglrxavpok.hephaistos.mca.AnvilException;
import org.jglrxavpok.hephaistos.mca.ChunkColumn;
import org.jglrxavpok.hephaistos.mca.RegionFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public class AnvilLoader implements AutoCloseable {
    private static final Map<String, Integer> BLOCK_MAPPING = new HashMap<>() {{
        put("minecraft:bedrock", Block.BEDROCK.getId());
        put("minecraft:dirt", Block.DIRT.getId());
        put("minecraft:podzol", Block.DIRT.getId());
        put("minecraft:grass", Block.GRASS.getId());
        put("minecraft:tall_grass", Block.GRASS.getId());
        put("minecraft:grass_block", Block.GRASS_BLOCK.getId());
        put("minecraft:stone", Block.STONE.getId());
        put("minecraft:cobblestone", Block.COBBLESTONE.getId());
        put("minecraft:sea_lantern", Block.SEA_LANTERN.getId());
        put("minecraft:magma", Block.MAGMA.getId());
        put("minecraft:ice", Block.ICE.getId());
        put("minecraft:clay", Block.CLAY.getId());
        put("minecraft:sand", Block.SAND.getId());
        put("minecraft:water", Block.WATER.getId());
        put("minecraft:flowing_water", Block.WATER.getId());
        put("minecraft:lava", Block.LAVA.getId());
        put("minecraft:flowing_lava", Block.LAVA.getId());
        put("minecraft:gravel", Block.GRAVEL.getId());
        put("minecraft:leaves", Block.LEAVES.getId());
        put("minecraft:birch_leaves", Block.LEAVES.getId());
        put("minecraft:oak_leaves", Block.LEAVES.getId());
        put("minecraft:bookshelf", Block.BOOKSHELF.getId());
        put("minecraft:diorite", Block.DIORITE.getId());
        put("minecraft:oak_wood", Block.OAK_WOOD.getId());
        put("minecraft:oak_log", Block.OAK_WOOD.getId());
        put("minecraft:obsidian", Block.OBSIDIAN.getId());
        put("minecraft:granite", Block.GRANITE.getId());
        put("minecraft:coarse_dirt", Block.DIRT.getId());
        put("minecraft:andesite", Block.ANDESITE.getId());
        put("minecraft:coal_ore", Block.COAL_ORE.getId());
        put("minecraft:lapis_ore", Block.LAPIS_ORE.getId());
        put("minecraft:redstone_ore", Block.REDSTONE_ORE.getId());
        put("minecraft:gold_ore", Block.GOLD_ORE.getId());
        put("minecraft:diamond_ore", Block.DIAMOND_ORE.getId());
        put("minecraft:emerald_ore", Block.EMERALD_ORE.getId());
        put("minecraft:iron_ore", Block.IRON_ORE.getId());
        put("minecraft:birch_wood", Block.BRICH_WOOD.getId());
        put("minecraft:mossy_cobblestone", Block.MOSSY_COBBLESTONE.getId());
        put("minecraft:wheat", Block.WHEAT_BLOCK.getId());
        put("minecraft:farmland", Block.FARMLAND_BLOCK.getId());
    }};

    private final File regionDir;
    private final Map<String, RegionFile> loadedRegions = new HashMap<>();
    private final Object2IntMap<String> invalidStatistics = new Object2IntOpenHashMap<>();

    public AnvilLoader(File saveDir) {
        this.regionDir = new File(saveDir, "region");
    }

    public IChunk loadChunk(int x, int y, int z) throws AnvilException, IOException {
        int regionX = x >> 5;
        int regionZ = z >> 5;

        RegionFile region = loadedRegions.computeIfAbsent(regionFileName(regionX, regionZ), name -> {
            try {
                String child = regionFileName(regionX, regionZ);
                File regionFile = new File(regionDir, child);
                RandomAccessFile accessFile = new RandomAccessFile(regionFile, "rw");
                return new RegionFile(accessFile, regionX, regionZ);
            } catch (AnvilException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        ChunkColumn column = region.getChunk(x, z);
        if (column == null) {
            return new EmptyChunk(x, y, z);
        }

        return readChunk(column, x, y, z);
    }

    private IChunk readChunk(ChunkColumn column, int x, int y, int z) {
        Chunk chunk = new Chunk(x, y, z);
        boolean empty = true;

        for (int iX = 0; iX < 16; iX++) {
            for (int iY = 0; iY < 16; iY++) {
                for (int iZ = 0; iZ < 16; iZ++) {
                    String name = column.getBlockState(iX, (y * 16) + iY, iZ).getName();
                    if (name.equals("minecraft:air")) {
                        continue;
                    }

                    empty = false;
                    Integer blockId = BLOCK_MAPPING.get(name);
                    if (blockId == null) {
                        blockId = Block.INVALID.getId();
                        invalidStatistics.computeInt(name, (k, v) -> v == null ? 1 : v + 1);
                    }

                    chunk.setBlockId(iX, iY, iZ, blockId);
                }
            }
        }

        if (empty) {
            return new EmptyChunk(x, y, z);
        }

        return chunk;
    }

    public String regionFileName(int regionX, int regionZ) {
        return "r." + regionX + "." + regionZ + ".mca";
    }

    public Object2IntMap<String> getInvalidStatistics() {
        return invalidStatistics;
    }

    @Override
    public void close() throws Exception {
        loadedRegions.forEach((k,v) -> {
            try {
                v.close();
            } catch (IOException ignored) {}
        });
    }
}
