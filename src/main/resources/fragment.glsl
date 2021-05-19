#version 330

in vec2 exTexCoord;

uniform mat4 projectionMatrix;

uniform sampler2D texture_sampler;

out vec4 fragColor;

void main() {
    fragColor = texture(texture_sampler, exTexCoord);
}
