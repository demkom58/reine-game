#version 330 core

uniform sampler2D tex;

in vec3 Pos;
in vec3 Face;

in vec4 TexCoords;
in vec2 TileUV;

out vec4 FragColor;

// Algorithm from a blog post
// http://0fps.net/2013/07/09/texture-atlases-wrapping-and-mip-mapping/
vec4 atlasTexture(vec2 tileOffset, vec2 tileUV, vec2 tileSize, sampler2D atlas) {
    // Initialize accumulators
    vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
    float totalWeight = 0.0;

    for (int dx = 0; dx < 2; ++dx) {
        for (int dy = 0; dy < 2; ++dy) {
            // Compute coordinate in 2x2 tile patch
            vec2 tileCoord = tileUV + vec2(dx, dy);

            // Weight sample based on distance to center
            float w = pow(1.0 - max(abs(tileCoord.x - 1.0), abs(tileCoord.y - 1.0)), 16.0);

            // Compute atlas coord
            vec2 atlasUV = tileOffset + tileSize * fract(tileCoord);

            // Sample and accumulate
            color += w * texture2D(atlas, atlasUV);
            totalWeight += w;
        }
    }

    // Return weighted color
    return color / totalWeight;
}

void main() {
    float u = dot(Face.yxz, Pos.xzx);
    float v = dot(Face.yxz, Pos.zyy);
    vec2 tileUV = fract(vec2(u, v));
    FragColor = atlasTexture(TexCoords.xy, TexCoords.zw, tileUV, tex);
}