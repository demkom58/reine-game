package com.crown.resource.image;

import com.crown.graphic.util.Destroyable;
import com.crown.util.PixelFormat;
import com.reine.util.CrownMath;
import org.lwjgl.stb.STBRPContext;
import org.lwjgl.stb.STBRPNode;
import org.lwjgl.stb.STBRPRect;
import org.lwjgl.system.MemoryStack;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.lwjgl.stb.STBRectPack.stbrp_init_target;
import static org.lwjgl.stb.STBRectPack.stbrp_pack_rects;
import static org.lwjgl.system.MemoryStack.stackPush;

public class AtlasImage implements Destroyable {
    private final GenericImageData data;
    private final Map<String, AtlasDimension> positions;

    private AtlasImage(GenericImageData data, Map<String, AtlasDimension> positions) {
        this.data = data;
        this.positions = positions;
    }

    public GenericImageData getData() {
        return data;
    }

    public Map<String, AtlasDimension> getPositions() {
        return positions;
    }

    public static AtlasImage create(int maxWidth, int maxHeight, Map<String, ImageInfo> textures) {
        final Map<String, AtlasDimension> dimensions = findPositions(maxWidth, maxHeight, textures);

        int width = 0;
        int height = 0;
        for (AtlasDimension rect : dimensions.values()) {
            width = Math.max(rect.x() + rect.width(), width);
            height = Math.max(rect.y() + rect.height(), height);
        }
        width = CrownMath.ceilToPowerOfTwo(width);
        height = CrownMath.ceilToPowerOfTwo(height);

        final PixelFormat format = fitFormat(textures.values());
        final GenericImageData atlas = GenericImageData.alloc(width, height, format);

        for (String key : textures.keySet()) {
            ImageInfo imageInfo = textures.get(key);
            try (StbiImageData texture = StbiImageData.load(imageInfo.file())) {
                insertTexture(texture, dimensions.get(key), atlas);
            }
        }

        return new AtlasImage(atlas, dimensions);
    }

    public static CompletableFuture<AtlasImage> createParallel(int maxWidth, int maxHeight, Map<String, ImageInfo> textures) {
        final Map<String, AtlasDimension> dimensions = findPositions(maxWidth, maxHeight, textures);

        int width = 0;
        int height = 0;
        for (AtlasDimension rect : dimensions.values()) {
            width = Math.max(rect.x() + rect.width(), width);
            height = Math.max(rect.y() + rect.height(), height);
        }
        width = CrownMath.ceilToPowerOfTwo(width);
        height = CrownMath.ceilToPowerOfTwo(height);

        final PixelFormat format = fitFormat(textures.values());
        final GenericImageData atlas = GenericImageData.alloc(width, height, format);

        CompletableFuture<?>[] insertions = new CompletableFuture[textures.size()];
        int i = 0;

        for (String key : textures.keySet()) {
            ImageInfo imageInfo = textures.get(key);

            insertions[i] = CompletableFuture.runAsync(() -> {
                try (StbiImageData texture = StbiImageData.load(imageInfo.file())) {
                     insertTexture(texture, dimensions.get(key), atlas);
                }
            });

            i++;
        }

        return CompletableFuture.allOf(insertions).thenApply(v -> new AtlasImage(atlas, dimensions));
    }

    private static Map<String, AtlasDimension> findPositions(
            int atlasMaxWidth, int atlasMaxHeight, Map<String, ImageInfo> textures) {

        STBRPNode.Buffer nodes = null;
        try (MemoryStack stack = stackPush()) {
            int size = textures.size();
            List<Map.Entry<String, ImageInfo>> ordered = new ArrayList<>(textures.entrySet());

            STBRPRect.Buffer rects = STBRPRect.calloc(size, stack);
            for (int i = 0; i < ordered.size(); i++) {
                final Map.Entry<String, ImageInfo> entry = ordered.get(i);
                final ImageInfo tex = entry.getValue();
                rects.position(i);
                rects.id(i);
                rects.w(tex.width());
                rects.h(tex.height());
            }
            rects.position(ordered.size());
            rects.flip();

            STBRPContext context = STBRPContext.calloc(stack);
            nodes = STBRPNode.calloc(atlasMaxWidth * atlasMaxHeight);

            stbrp_init_target(context, atlasMaxWidth, atlasMaxHeight, nodes);
            stbrp_pack_rects(context, rects);

            Map<String, AtlasDimension> dimensions = new HashMap<>(size);
            while (rects.hasRemaining()) {
                STBRPRect r = rects.get();
                if (!r.was_packed()) {
                    throw new IllegalArgumentException("Failed to pack all textures to atlas.");
                }

                String id = ordered.get(r.id()).getKey();
                dimensions.put(id, new AtlasDimension(r.x(), r.y(), r.w(), r.h()));
            }

            return dimensions;
        } finally {
            if (nodes != null) {
                nodes.free();
            }
        }
    }

    private static void insertTexture(ImageData texture, AtlasDimension dim, ImageData atlas) {
        final int width = texture.width();
        final int height = texture.height();

        final int tX = dim.x();
        final int tY = dim.y();

        int lastIndexY = height - 1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgba = texture.getRGBA(x, y);
                atlas.setRGBA(tX + x, tY + (lastIndexY - y), rgba);
            }
        }
    }

    private static PixelFormat fitFormat(Collection<ImageInfo> textures) {
        Map<ImageInfo, Integer> formats = new HashMap<>();
        for (ImageInfo data : textures) {
            formats.compute(data, (k, i) -> (i == null ? 0 : i) + 1);
        }

        return formats.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .map(ImageInfo::format)
                .orElse(PixelFormat.RGBA);
    }

    @Override
    public void destroy() {
        data.destroy();
    }
}
