#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;

uniform float aspect;
uniform float scale;

out vec2 exTexCoord;

void main() {
    exTexCoord = texCoord;

    gl_Position =  vec4(position.x / aspect * scale, position.y * scale, position.z, 1.0);
}
