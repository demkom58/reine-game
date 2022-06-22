package com.reine.client.render.chunk;

import com.crown.graphic.gl.attribute.GlDataType;
import com.crown.graphic.gl.attribute.GlVertexFormat;
import com.reine.block.Block;
import com.reine.client.TextureManager;
import com.reine.client.render.util.Vertices;
import com.reine.util.WorldSide;
import org.joml.Vector3i;

import java.nio.ByteBuffer;
import java.util.Collection;

public final class ChunkFormat {

    enum Attribute {
        POSITION,
        FACE
    }

    public static final GlVertexFormat<Attribute> CHUNK_FORMAT = GlVertexFormat.builder(Attribute.class, 28)
            .add(Attribute.POSITION, 0, GlDataType.FLOAT, 3, false)
            .add(Attribute.FACE, 12, GlDataType.INT, 4, true)
            .build();

    public static void write(TextureManager textureManager, Collection<ChunkQuad> quads, ByteBuffer data) {
        float[] vertices = new float[6 * 3];
        for (ChunkQuad quad : quads) {
            final WorldSide side = quad.side();
            Vertices.quad(side, quad.start(), quad.end(), vertices);

            final int texId = textureManager.getId(Block.byId(quad.blockId()).getTexture(side));
            final Vector3i normal = side.vec();

            for (int i = 0; i < 6; i++) {
                int vertexOffset = i * 3;
                data
                        .putFloat(vertices[vertexOffset])
                        .putFloat(vertices[vertexOffset + 1])
                        .putFloat(vertices[vertexOffset + 2])

                        .putInt(normal.x)
                        .putInt(normal.y)
                        .putInt(normal.z)
                        .putInt(texId);
            }
        }
    }
}
