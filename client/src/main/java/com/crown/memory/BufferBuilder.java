package com.crown.memory;

import com.crown.graphic.util.Destroyable;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public abstract class BufferBuilder implements Destroyable {
    protected ByteBuffer buffer;
    protected int count;

    private boolean building;

    /**
     * @param size size of buffer in bytes
     */
    protected BufferBuilder(int size) {
        this.buffer = MemoryUtil.memAlloc(size);
    }

    public void begin() {
        this.buffer.clear();
        this.count = 0;
        this.building = true;
    }

    public void end() {
        this.building = false;
    }

    @Override
    public void destroy() {
        MemoryUtil.memFree(this.buffer);
    }

    public int getCount() {
        return count;
    }

    public boolean isBuilding() {
        return building;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
