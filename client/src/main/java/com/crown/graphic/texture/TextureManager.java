package com.crown.graphic.texture;

import com.crown.resource.image.*;
import com.crown.resource.image.filter.DownscaleKernel;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.lwjgl.opengl.GL33.GL_MAX_TEXTURE_SIZE;
import static org.lwjgl.opengl.GL33.glGetInteger;
import static org.lwjgl.system.MemoryStack.stackPush;

public class TextureManager {
    private final File texturesRoot = new File("assets/textures/");
    private final Set<ImageResource> textureResources = new HashSet<>();
    private final Map<String, ImageDimension> nameDim = new HashMap<>();
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

            List<AtlasImage> atlases = new ArrayList<>();
            try  {
                final AtlasImage image = AtlasImage.createParallel(maxTextureSize, maxTextureSize, textures).get();
                atlases.add(image);

                for (int mipmapLevel = 1; mipmapLevel < 5; mipmapLevel++) {
                    atlases.add(AtlasImage.mipmap(DownscaleKernel.BLACKMAN_SINC, image, mipmapLevel));
                }

                long generatedFor = System.currentTimeMillis() - genStart;
                System.out.println("Atlas generation done for " + (generatedFor / 1000f) + "sec");

                nameDim.clear();
                nameDim.putAll(image.getPositions());

                // todo: add caching
                if (!textureResources.isEmpty()) {
                    for (int i = 0; i < atlases.size(); i++) {
                        AtlasImage img = atlases.get(i);
                        GenericImageData data = img.getData();
                        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("atlas-" + i + ".png"))) {
                            ImageIO.write(data.toBufferedImage(), "png", out);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                atlas = TextureAtlas2D.from(atlases);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                for (AtlasImage atlasImage : atlases) {
                    atlasImage.destroy();
                }
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
