package com.crown.graphic.texture;

import com.crown.resource.image.AtlasDimension;
import com.crown.resource.image.AtlasImage;
import com.crown.resource.image.ImageInfo;
import com.crown.resource.image.ImageResource;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.lwjgl.opengl.GL33.GL_MAX_TEXTURE_SIZE;
import static org.lwjgl.opengl.GL33.glGetInteger;
import static org.lwjgl.system.MemoryStack.stackPush;

public class TextureManager {
    private final File texturesRoot = new File("assets/textures/");
    private final Set<ImageResource> textureResources = new HashSet<>();
    private final Map<String, AtlasDimension> nameDim = new HashMap<>();
    private final Map<String, float[]> nameNormDim = new HashMap<>();

    private TextureAtlas2D atlas;
    private boolean atlasChanged = true;

    public TextureManager() {
    }

    public void registerTexture(String name) {
        boolean added = textureResources.add(new ImageResource(name));
        if (added) {
            atlasChanged = true;
        }
    }

    public float u(String id, int x) {
        return atlas.u(id, x);
    }

    public float v(String id, int v) {
        return atlas.v(id, v);
    }

    public Vector2f uv(String id, int u, int v) {
        return atlas.uv(id, u, v);
    }

    public void buildAtlas() {
        final int maxTextureSize = glGetInteger(GL_MAX_TEXTURE_SIZE);

        final Map<String, ImageInfo> textures = new HashMap<>();
        try (MemoryStack stack = stackPush()) {
            IntBuffer widthBuff = stack.mallocInt(1);
            IntBuffer heightBuff = stack.mallocInt(1);
            IntBuffer channelBuff = stack.mallocInt(1);

            for (ImageResource resource : textureResources) {
                final String name = resource.name();
                final ImageInfo info = ImageInfo.read(new File(texturesRoot, name),
                        widthBuff.rewind(), heightBuff.rewind(), channelBuff.rewind());
                textures.put(name, info);
            }

            long genStart = System.currentTimeMillis();
            CompletableFuture<AtlasImage> futureAtlas = AtlasImage.createParallel(maxTextureSize, maxTextureSize, textures);
            try (AtlasImage atlasImage = futureAtlas.get()) {
                long generatedFor = System.currentTimeMillis() - genStart;
                System.out.println("Atlas generation done for " + (generatedFor / 1000f) + "sec");

                nameDim.clear();
                nameDim.putAll(atlasImage.getPositions());

                // todo: add caching
                if (!textureResources.isEmpty()) {
                    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("atlas.png"))) {
                        ImageIO.write(atlasImage.getData().toBufferedImage(), "png", out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                atlas = TextureAtlas2D.from(atlasImage);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            nameNormDim.clear();
            nameDim.forEach((k, v) -> {
                float atlasWidth = atlas.getWidth();
                float atlasHeight = atlas.getHeight();

                int tileX = v.x();
                int tileY = v.y();

                float x = tileX / atlasWidth;
                float y = tileY / atlasHeight;
                float w = (tileX + v.width()) / atlasWidth - x;
                float h = (tileY + v.height()) / atlasHeight - y;


                nameNormDim.put(k, new float[]{x, y, w, h});
            });
        }

        atlasChanged = false;
    }

    public boolean isAtlasChanged() {
        return atlasChanged;
    }

    public float[] normalizedDimension(String name) {
        return nameNormDim.get(name);
    }

    public TextureAtlas2D getAtlas() {
        return atlas;
    }
}
