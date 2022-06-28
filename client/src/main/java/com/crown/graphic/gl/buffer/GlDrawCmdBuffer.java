package com.crown.graphic.gl.buffer;

import com.crown.memory.BufferBuilder;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL31.GL_COPY_WRITE_BUFFER;
import static org.lwjgl.opengl.GL45.GL_DRAW_INDIRECT_BUFFER;

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
        ByteBuffer buf = null;
        try {
            buf = MemoryUtil.memAlloc(COMMAND_STRUCT_BYTES);

            buf.putInt(count);         // Vertex Count
            buf.putInt(instanceCount); // Instance Count
            buf.putInt(first);         // Vertex Start
            buf.putInt(baseInstance);  // Base Instance

            int writeOffset = COMMAND_STRUCT_BYTES * idx;
            bind(GL_COPY_WRITE_BUFFER);
            upload(GL_COPY_WRITE_BUFFER, writeOffset, buf.flip());
            unbind(GL_COPY_WRITE_BUFFER);
        } finally {
            if (buf != null) {
                MemoryUtil.memFree(buf);
            }
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

        public void setIndirectDrawCall(int index, int first, int count, int baseInstance, int instanceCount) {
            ByteBuffer buf = this.buffer;

            final int writeOffset = COMMAND_STRUCT_BYTES * index;
            buf.putInt(writeOffset, count);                    // Vertex Count
            buf.putInt(writeOffset + 4, instanceCount);  // Instance Count
            buf.putInt(writeOffset + 8, first);          // Vertex Start
            buf.putInt(writeOffset + 12, baseInstance);  // Base Instance
        }

        public int getCapacity() {
            return capacity;
        }

        public GlDrawCmdBuffer build(int hints) {
            GlDrawCmdBuffer result = new GlDrawCmdBuffer(hints);
            result.bind();
            result.upload(GL_DRAW_INDIRECT_BUFFER, this.buffer.limit(writeOffset));
            result.unbind();
            return result;
        }
    }

}
