package com.reine.client.render.chunk;

import com.crown.graphic.gl.attribute.GlDataType;
import com.crown.graphic.gl.attribute.GlVertexFormat;
import com.reine.block.Block;
import com.reine.client.TextureManager;
import com.reine.client.render.chunk.util.ChunkQuad;
import com.reine.client.render.util.Vertices;
import com.reine.util.WorldSide;
import org.joml.Vector3b;

import java.nio.ByteBuffer;
import java.util.Collection;

public final class ChunkFormat {

    enum Attribute {
        POSITION,
        FACE,
        TEXTURE
    }

    public static final GlVertexFormat<Attribute> CHUNK_FORMAT = GlVertexFormat.builder(Attribute.class, 10)
            .add(Attribute.POSITION, 0, GlDataType.BYTE, 3, true)
            .add(Attribute.FACE, 3, GlDataType.BYTE, 3, true)
            .add(Attribute.TEXTURE, 6, GlDataType.UNSIGNED_INT, 1, true)
            .build();

    public static void write(TextureManager textureManager, Collection<ChunkQuad> quads, ByteBuffer data) {
        byte[] vertices = new byte[6 * 3];
        for (ChunkQuad quad : quads) {
            final WorldSide side = quad.side();
            Vertices.quad(side, quad.start(), quad.end(), vertices);

            final int texId = textureManager.getId(Block.byId(quad.blockId()).getTexture(side));
            final Vector3b normal = side.vec();

            for (int i = 0; i < 6; i++) {
                int vertexOffset = i * 3;
                data
                        .put(vertices[vertexOffset])
                        .put(vertices[vertexOffset + 1])
                        .put(vertices[vertexOffset + 2])

                        .put(normal.x)
                        .put(normal.y)
                        .put(normal.z)

                        .putInt(texId);
            }
        }
    }
}
