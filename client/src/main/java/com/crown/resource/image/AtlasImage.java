package com.crown.resource.image;

import com.crown.graphic.util.Destroyable;
import com.crown.resource.image.filter.DownscaleKernel;
import com.crown.resource.image.filter.ImageSampler;
import com.crown.util.ImageUtil;
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
    private final Map<String, ImageDimension> positions;

    private AtlasImage(GenericImageData data, Map<String, ImageDimension> positions) {
        this.data = data;
        this.positions = positions;
    }

    public GenericImageData getData() {
        return data;
    }

    public Map<String, ImageDimension> getPositions() {
        return positions;
    }

    public static AtlasImage create(int maxWidth, int maxHeight, Map<String, ImageInfo> textures) {
        final Map<String, ImageDimension> dimensions = findPositions(maxWidth, maxHeight, textures);

        int width = 0;
        int height = 0;
        for (ImageDimension rect : dimensions.values()) {
            width = Math.max(rect.x() + rect.width(), width);
            height = Math.max(rect.y() + rect.height(), height);
        }
        width = CrownMath.ceilToPowerOfTwo(width);
        height = CrownMath.ceilToPowerOfTwo(height);

        final PixelFormat format = ImageUtil.fitFormat(textures.values());
        final GenericImageData atlas = GenericImageData.alloc(width, height, format);

        for (String key : textures.keySet()) {
            ImageInfo imageInfo = textures.get(key);
            try (StbiImageData texture = StbiImageData.load(imageInfo.file())) {
                ImageUtil.insert(texture, dimensions.get(key), atlas);
            }
        }

        return new AtlasImage(atlas, dimensions);
    }

    public static CompletableFuture<AtlasImage> createParallel(int maxWidth, int maxHeight, Map<String, ImageInfo> textures) {
        final Map<String, ImageDimension> dimensions = findPositions(maxWidth, maxHeight, textures);

        int width = 0;
        int height = 0;
        for (ImageDimension rect : dimensions.values()) {
            width = Math.max(rect.x() + rect.width(), width);
            height = Math.max(rect.y() + rect.height(), height);
        }
        width = CrownMath.ceilToPowerOfTwo(width);
        height = CrownMath.ceilToPowerOfTwo(height);

        final PixelFormat format = ImageUtil.fitFormat(textures.values());
        final GenericImageData atlas = GenericImageData.alloc(width, height, format);

        final CompletableFuture<?>[] insertions = new CompletableFuture[textures.size()];
        int i = 0;

        for (String key : textures.keySet()) {
            ImageInfo imageInfo = textures.get(key);

            insertions[i] = CompletableFuture.runAsync(() -> {
                try (StbiImageData texture = StbiImageData.load(imageInfo.file())) {
                    ImageUtil.insert(texture, dimensions.get(key), atlas);
                }
            });

            i++;
        }

        return CompletableFuture.allOf(insertions).thenApplyAsync(v -> new AtlasImage(atlas, dimensions));
    }

    public static AtlasImage mipmap(DownscaleKernel kernel, AtlasImage atlas, int level) {
        if (level == 0) {
            return atlas;
        }

        final Map<String, ImageDimension> mappedPositions = new HashMap<>();
        final int div = (int) Math.pow(2 , level);

        final GenericImageData sourceData = atlas.data;
        final int newWidth = sourceData.width() / div;
        final int newHeight = sourceData.height() / div;

        final GenericImageData mappedData = GenericImageData.alloc(newWidth, newHeight, sourceData.format());
        atlas.positions.forEach((k, dim) -> {
            final ImageDimension newDim = new ImageDimension(
                    dim.x() / div, dim.y() / div, dim.width() / div, dim.height() / div);
            mappedPositions.put(k, newDim);

            FramedImageData prev = new FramedImageData(sourceData, dim);
            FramedImageData cur = new FramedImageData(mappedData, newDim);

            ImageSampler.sample(kernel, prev, cur);
        });

        return new AtlasImage(mappedData, mappedPositions);
    }

    private static Map<String, ImageDimension> findPositions(
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
            nodes = STBRPNode.calloc(textures.size());

            stbrp_init_target(context, atlasMaxWidth, atlasMaxHeight, nodes);
            stbrp_pack_rects(context, rects);

            Map<String, ImageDimension> dimensions = new HashMap<>(size);
            while (rects.hasRemaining()) {
                STBRPRect r = rects.get();
                if (!r.was_packed()) {
                    throw new IllegalArgumentException("Failed to pack all textures to atlas.");
                }

                String id = ordered.get(r.id()).getKey();
                dimensions.put(id, new ImageDimension(r.x(), r.y(), r.w(), r.h()));
            }

            return dimensions;
        } finally {
            if (nodes != null) {
                nodes.free();
            }
        }
    }

    @Override
    public void destroy() {
        data.destroy();
    }
}
