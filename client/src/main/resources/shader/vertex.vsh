#version 330 core

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aFace;
layout (location = 2) in vec4 aTexCoords;

out vec2 TileUV;
out vec4 TexCoords;

void main()
{
    gl_Position = projection * view * model * vec4(aPos, 1.0);

    TileUV = vec2(dot(aFace.yxz, aPos.xzx), dot(aFace.yxz, aPos.zyy));

    TexCoords = aTexCoords;
}