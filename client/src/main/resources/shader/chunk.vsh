layout(location = 0) uniform mat4 model;
layout(location = 1) uniform mat4 view;
layout(location = 2) uniform mat4 projection;

layout(location = 0) in vec3 aPos;
layout(location = 1) in ivec4 aFace;

out vec3 Pos;
out flat ivec4 Face;

void main()
{
    gl_Position = projection * view * model * vec4(aPos, 1.0);

    Pos = aPos;
    Face = aFace;
}