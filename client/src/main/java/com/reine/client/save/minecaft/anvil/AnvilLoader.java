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
    private static final Object2IntMap<String> BLOCK_MAPPING = new Object2IntOpenHashMap<>() {{
        put("minecraft:bedrock", Block.BEDROCK.getId());
        put("minecraft:dirt", Block.DIRT.getId());
        put("minecraft:grass", Block.GRASS.getId());
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
        put("minecraft:bookshelf", Block.BOOKSHELF.getId());
    }};

    private final File regionDir;
    private Map<String, RegionFile> loadedRegions = new HashMap<>();

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
                    chunk.setBlockId(iX, iY, iZ, BLOCK_MAPPING.getOrDefault(name, Block.INVALID.getId()));
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

    @Override
    public void close() throws Exception {
        loadedRegions.forEach((k,v) -> {
            try {
                v.close();
            } catch (IOException ignored) {}
        });
    }
}
