package com.reine.client.render.chunk;

import com.crown.graphic.gl.attribute.GlDataType;
import com.crown.graphic.gl.attribute.GlVertexFormat;
import com.reine.block.Block;
import com.reine.client.TextureManager;
import com.reine.util.WorldSide;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.util.Collection;

public final class ChunkFormat {
    public static final GlVertexFormat<Attribute> CHUNK_FORMAT = GlVertexFormat.builder(Attribute.class, 28)
            .add(Attribute.POSITION, 0, GlDataType.FLOAT, 3, false)
            .add(Attribute.FACE, 12, GlDataType.INT, 4, true)
            .build();

    public static void write(TextureManager textureManager, Collection<ChunkQuad> quads, ByteBuffer data) {
        for (ChunkQuad quad : quads) {
            float[] vertices = getVertices(quad);

            final WorldSide side = quad.side();
            final int texId = textureManager.getId(Block.byId(quad.blockId()).getTexture(side));
            final Vector3f normal = side.vec();

            int[] normals = new int[]{(int) normal.x, (int) normal.y, (int) normal.z, texId};

            for (int i = 0; i < 6; i++) {
                int vertexOffset = i * 3;
                data
                        .putFloat(vertices[vertexOffset])
                        .putFloat(vertices[vertexOffset + 1])
                        .putFloat(vertices[vertexOffset + 2])

                        .putInt(normals[0])
                        .putInt(normals[1])
                        .putInt(normals[2])
                        .putInt(normals[3]);
            }
        }
    }

    private static float[] getVertices(ChunkQuad quad) {
        WorldSide side = quad.side();
        Vector3f str = quad.start();
        Vector3f end = quad.end();

        return switch (side) {
            case WEST -> new float[]{
                    str.x, str.y, str.z,
                    str.x, str.y, end.z,
                    str.x, end.y, str.z,

                    str.x, end.y, str.z,
                    str.x, str.y, end.z,
                    str.x, end.y, end.z,
            };
            case EAST -> new float[]{
                    str.x, str.y, str.z,
                    str.x, end.y, str.z,
                    str.x, str.y, end.z,

                    str.x, str.y, end.z,
                    str.x, end.y, str.z,
                    str.x, end.y, end.z,
            };
            case DOWN -> new float[]{
                    str.x, str.y, str.z,
                    end.x, str.y, str.z,
                    str.x, str.y, end.z,

                    str.x, str.y, end.z,
                    end.x, str.y, str.z,
                    end.x, str.y, end.z,
            };
            case UP -> new float[]{
                    str.x, str.y, str.z,
                    str.x, str.y, end.z,
                    end.x, str.y, str.z,

                    end.x, str.y, str.z,
                    str.x, str.y, end.z,
                    end.x, str.y, end.z,
            };
            case NORTH -> new float[]{
                    end.x, str.y, str.z,
                    str.x, str.y, str.z,
                    str.x, end.y, str.z,

                    end.x, str.y, str.z,
                    str.x, end.y, str.z,
                    end.x, end.y, str.z,
            };
            case SOUTH -> new float[]{
                    str.x, str.y, str.z,
                    end.x, str.y, str.z,
                    str.x, end.y, str.z,

                    str.x, end.y, str.z,
                    end.x, str.y, str.z,
                    end.x, end.y, str.z,
            };
        };
    }

    enum Attribute {
        POSITION,
        FACE
    }
}
