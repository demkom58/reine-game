package com.crown.graphic.gl.buffer;

import com.crown.memory.BufferBuilder;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL45.*;

public class GlDrawCmdBuffer extends GlMutableBuffer {
    private static final int COMMAND_STRUCT_BYTES = 4 * 4;

    public GlDrawCmdBuffer(int hints) {
        super(hints);
    }

    public void bind() {
        super.bind(GL_DRAW_INDIRECT_BUFFER);
    }

    public void unbind() {
        super.unbind(GL_DRAW_INDIRECT_BUFFER);
    }

    public void setIndirectDrawCall(int idx, int first, int count, int baseInstance, int instanceCount) {
        try (MemoryStack stack = MemoryStack.stackGet()) {
            ByteBuffer buf = stack.malloc(COMMAND_STRUCT_BYTES);

            buf.putInt(count);         // Vertex Count
            buf.putInt(instanceCount); // Instance Count
            buf.putInt(first);         // Vertex Start
            buf.putInt(baseInstance);  // Base Instance

            int writeOffset = COMMAND_STRUCT_BYTES * idx;
            upload(GL_DRAW_INDIRECT_BUFFER, writeOffset, buf);
        }
    }

    public static Builder builder(int capacity) {
        return new Builder(capacity);
    }

    public static class Builder extends BufferBuilder {
        protected final int capacity;
        private int writeOffset = 0;

        private Builder(int capacity) {
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

        public int getCapacity() {
            return capacity;
        }

        public GlDrawCmdBuffer build(int hints) {
            GlDrawCmdBuffer result = new GlDrawCmdBuffer(hints);
            result.bind();
            result.upload(GL_DRAW_INDIRECT_BUFFER, this.buffer);
            result.unbind();
            return result;
        }
    }


}
