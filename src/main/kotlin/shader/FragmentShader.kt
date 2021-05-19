package shader

import org.lwjgl.opengl.GL20

class FragmentShader(
    shaderCode: String,
    programId: Int
) : Shader(
    shaderCode,
    GL20.GL_FRAGMENT_SHADER,
    programId
) {
}