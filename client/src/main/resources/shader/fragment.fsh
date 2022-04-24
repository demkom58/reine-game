#version 330 core

uniform sampler2D tex;

in vec4 TexCoords;
in vec2 TileUV;

out vec4 FragColor;

vec4 atlasTexture(vec2 tileOffset, vec2 tileSize, vec2 tileUV, sampler2D atlas) {
    return texture(tex, tileOffset + fract(tileUV) * tileSize);
    // Initialize accumulators
    vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
    float totalWeight = 0.0;

    for (int dx = 0; dx < 2; ++dx) {
        for (int dy = 0; dy < 2; ++dy) {
            // Compute coordinate in 2x2 tile patch
            vec2 tileCoord = 2.0 * fract(0.5 * (tileUV * vec2(dx, dy)));

            // Weight sample based on distance to center
            float w = pow(1.0 - max(abs(tileCoord.x - 1.0), abs(tileCoord.y - 1.0)), 16.0);

            // Compute atlas coord
            vec2 atlasUV = tileOffset + tileSize * tileCoord;

            // Sample and accumulate
            color += w * texture2D(atlas, atlasUV);
            totalWeight += w;
        }
    }

    // Return weighted color
    return color / totalWeight;
}

void main()
{
    FragColor = atlasTexture(TexCoords.xy, TexCoords.zw, TileUV, tex);
}