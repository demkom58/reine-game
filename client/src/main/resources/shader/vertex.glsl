#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 vertexColor;

out vec4 color;

uniform float time;

void main()
{
    float cos = cos(time);
    float sin = sin(time);
    gl_Position = vec4(sin + aPos.x, cos + aPos.y, aPos.z, 1.0);
    color = vec4(aPos.x, (cos + vertexColor.g) / 2.0f, aPos.z, 1.0f);
}