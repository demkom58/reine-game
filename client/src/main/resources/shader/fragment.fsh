#version 330 core

uniform sampler2D tex;

in vec4 TexCoords;
in vec2 TileUV;

out vec4 FragColor;

vec4 atlasTexture(vec2 tileOffset, vec2 tileSize, vec2 tileUV, sampler2D atlas) {
    return texture(tex, tileOffset + fract(tileUV) * tileSize);
}

void main()
{
    FragColor = atlasTexture(TexCoords.xy, TexCoords.zw, TileUV, tex);
}