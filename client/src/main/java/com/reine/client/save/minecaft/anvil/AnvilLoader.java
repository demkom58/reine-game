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
    public static final int MC_CHUNK_AXIS = 16;
    public static final int WIDTH_BITS_DIF = Long.bitCount(IChunk.CHUNK_WIDTH - 1) - Long.bitCount(MC_CHUNK_AXIS - 1);
    public static final int HEIGHT_BITS_DIF = Long.bitCount(IChunk.CHUNK_HEIGHT - 1) - Long.bitCount(MC_CHUNK_AXIS - 1);
    public static final int LENGTH_BITS_DIF = Long.bitCount(IChunk.CHUNK_LENGTH - 1) - Long.bitCount(MC_CHUNK_AXIS - 1);

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
    private final Block invalidBlock;
    private final Map<String, RegionFile> loadedRegions = new HashMap<>();
    private final Object2IntMap<String> invalidStatistics = new Object2IntOpenHashMap<>();

    public AnvilLoader(File saveDir, Block invalidBlock) {
        this.regionDir = new File(saveDir, "region");
        this.invalidBlock = invalidBlock;
    }

    public IChunk loadChunk(int rX, int rY, int rZ) throws AnvilException, IOException {
        int x = rX << WIDTH_BITS_DIF;
        int y = rY << HEIGHT_BITS_DIF;
        int z = rZ << LENGTH_BITS_DIF;

        final Chunk chunk = new Chunk(rX, rY, rZ);

        boolean hasCubes = false;
        for (int wX = 0; wX < IChunk.CHUNK_WIDTH / MC_CHUNK_AXIS; wX++) {
            for (int wY = 0; wY < IChunk.CHUNK_HEIGHT / MC_CHUNK_AXIS; wY++) {
                for (int wZ = 0; wZ < IChunk.CHUNK_LENGTH / MC_CHUNK_AXIS; wZ++) {

                    int mcX = x + wX,
                            mcY = y + wY,
                            mcZ = z + wZ;

                    hasCubes |= readChunk(
                            chunk,
                            wX * MC_CHUNK_AXIS,
                            wY * MC_CHUNK_AXIS,
                            wZ * MC_CHUNK_AXIS,
                            mcX, mcY, mcZ
                    );
                }
            }
        }

        if (!hasCubes) {
            return new EmptyChunk(rX, rY, rZ);
        }

        return chunk;
    }


    public boolean readChunk(Chunk chunk, int startX, int startY, int startZ, int x, int y, int z) throws AnvilException, IOException {
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

        return readChunk(chunk, region.getChunk(x, z), startX, startY, startZ, y);
    }

    private boolean readChunk(Chunk chunk, ChunkColumn column, int startX, int startY, int startZ, int chunkY) {
        boolean writed = false;

        for (int iX = 0; iX < MC_CHUNK_AXIS; iX++) {
            for (int iY = 0; iY < MC_CHUNK_AXIS; iY++) {
                for (int iZ = 0; iZ < MC_CHUNK_AXIS; iZ++) {
                    String name = column.getBlockState(iX, (chunkY * MC_CHUNK_AXIS) + iY, iZ).getName();
                    if (name.equals("minecraft:air")) {
                        continue;
                    }

                    writed = true;
                    Integer blockId = BLOCK_MAPPING.get(name);
                    if (blockId == null) {
                        blockId = invalidBlock.getId();
                        invalidStatistics.computeInt(name, (k, v) -> v == null ? 1 : v + 1);
                    }

                    chunk.setBlockId(startX + iX, startY + iY, startZ + iZ, blockId);
                }
            }
        }

        return writed;
    }

    public String regionFileName(int regionX, int regionZ) {
        return "r." + regionX + "." + regionZ + ".mca";
    }

    public Object2IntMap<String> getInvalidStatistics() {
        return invalidStatistics;
    }

    @Override
    public void close() throws Exception {
        loadedRegions.forEach((k, v) -> {
            try {
                v.close();
            } catch (IOException ignored) {
            }
        });
    }
}
