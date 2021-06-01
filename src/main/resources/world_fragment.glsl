#version 330

in vec2 exTexCoord;
in vec4 exColor;

uniform mat4 projectionMatrix;
uniform sampler2D texture_sampler;
uniform bool usesTextures;

out vec4 fragColor;

void main() {
    if (usesTextures) {
        vec4 textureColor = texture(texture_sampler, exTexCoord);
        if (textureColor.a < 0.05)
        discard;
        fragColor = texture(texture_sampler, exTexCoord);
    } else {
        fragColor = vec4(exColor);
    }

}
