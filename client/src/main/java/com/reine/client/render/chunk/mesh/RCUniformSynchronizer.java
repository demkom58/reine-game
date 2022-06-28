package com.reine.client.render.chunk.mesh;

import com.crown.graphic.gl.buffer.GlMutableBuffer;
import com.crown.graphic.util.ListHandler;
import com.reine.block.BlockLayer;
import com.reine.world.chunk.ChunkPosition;

import static com.reine.client.render.chunk.mesh.RenderChunk.UNIFORM_BUFFER_SIZE;
import static com.reine.client.render.chunk.mesh.RenderChunk.UNIFORM_MATRIX_SIZE;
import static org.lwjgl.opengl.GL31.*;

public class RCUniformSynchronizer implements ListHandler<Integer> {
    private final ChunkPosition pos;
    private final RenderChunk chunk;
    private BlockLayer layer;

    public RCUniformSynchronizer(ChunkPosition pos, RenderChunk chunk) {
        this.pos = pos;
        this.chunk = chunk;
    }

    public void setLayer(BlockLayer layer) {
        this.layer = layer;
    }

    @Override
    public void added(int drawId, Integer chunkIndex) {
        chunk.setChunkPosition(layer, drawId, this.pos);
    }

    @Override
    public void removed(int drawId, Integer chunkIndex) {
        GlMutableBuffer modelUniform = chunk.modelUniforms().get(layer);

        int writeOffset = drawId * UNIFORM_MATRIX_SIZE;
        int readOffset = (drawId + 1) * UNIFORM_MATRIX_SIZE;
        int size = UNIFORM_BUFFER_SIZE - writeOffset;
        if (size == 0) {
            return;
        }

        modelUniform.bind(GL_COPY_READ_BUFFER);
        modelUniform.bind(GL_COPY_WRITE_BUFFER);

        glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, readOffset, writeOffset, size);

        modelUniform.unbind(GL_COPY_WRITE_BUFFER);
        modelUniform.unbind(GL_COPY_READ_BUFFER);
    }
}
