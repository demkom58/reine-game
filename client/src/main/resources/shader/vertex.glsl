#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 vertexColor;
layout (location = 2) in vec2 texCoords;

out vec4 Color;
out vec2 TexCoords;

uniform float time;

void main()
{
    gl_Position = vec4(aPos, 1.0);
    Color = vec4(vertexColor, 1.0);
    TexCoords = texCoords;
}