package com.crown.graphic.gl.buffer;

import com.crown.memory.BufferBuilder;

import java.nio.ByteBuffer;

public class GlDrawCallBatcher extends BufferBuilder {
    private static final int COMMAND_STRUCT_BYTES = 4 * 4;
    protected final int capacity;
    private int writeOffset = 0;

    protected GlDrawCallBatcher(int capacity) {
        super(capacity * COMMAND_STRUCT_BYTES);
        this.capacity = capacity;
    }

    @Override
    public void begin() {
        super.begin();
        this.writeOffset = 0;
    }

    public void addIndirectDrawCall(int first, int count, int baseInstance, int instanceCount) {
        ByteBuffer buf = this.buffer;

        buf.putInt(this.writeOffset, count);                    // Vertex Count
        buf.putInt(this.writeOffset + 4, instanceCount);  // Instance Count
        buf.putInt(this.writeOffset + 8, first);          // Vertex Start
        buf.putInt(this.writeOffset + 12, baseInstance);  // Base Instance

        this.writeOffset += COMMAND_STRUCT_BYTES;
        this.count++;
    }
}
