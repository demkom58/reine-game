layout(location = 0) uniform mat4 model;
layout(location = 1) uniform mat4 view;
layout(location = 2) uniform mat4 projection;

layout(location = 0) in vec3 aPos;

void main()
{
    gl_Position = projection * view * model * vec4(aPos, 1.0);
}