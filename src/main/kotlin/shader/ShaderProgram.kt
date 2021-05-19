package shader

import org.joml.Matrix4f
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryStack

class ShaderProgram {
    val programId: Int = glCreateProgram()
    private val shaderIdMap: MutableMap<Shader, Int> = mutableMapOf()
    private val uniformMap: MutableMap<String, Int> = mutableMapOf()

    init {
        if (programId == 0) {
            throw RuntimeException("Couldn't create shader program")
        }
    }

    fun link() {
        glLinkProgram(programId)

        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            println("Error linking shader code: ${glGetProgramInfoLog(programId, 1024)} ")
        }

        shaderIdMap.forEach { (k, v) ->
            if (v != 0) {
                k.detach()
            }
        }

        glValidateProgram(programId)
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: ${glGetProgramInfoLog(programId, 1024)}")
        }
    }

    fun registerShader(shader: Shader) {
        shaderIdMap[shader] = shader.shaderId
    }

    fun bind() {
        glUseProgram(programId)
    }

    fun unbind() {
        glUseProgram(0)
    }

    fun createUniform(name: String): ShaderProgram {
        if (uniformMap.containsKey(name)) return this
        val loc: Int = glGetUniformLocation(programId, name)

        if (loc < 0) {
            throw RuntimeException("Couldn't find uniform: $name")
        }

        uniformMap[name] = loc
        return this
    }

    fun setBool(name: String, value: Boolean) {
        glUniform1i(uniformMap[name]!!, if (value) 1 else 0)
    }

    fun setInt(name: String, value: Int) {
        glUniform1i(uniformMap[name]!!, value)
    }

    fun setFloat(name: String, value: Float) {
        glUniform1f(uniformMap[name]!!, value)
    }

    fun setMatrix4f(name: String, value: Matrix4f) {
        MemoryStack.stackPush().use { stack ->
            uniformMap[name]?.let {
                glUniformMatrix4fv(
                    it, false,
                    value[stack.mallocFloat(16)]
                )
            }
        }
    }

    fun cleanup() {
        unbind()

        if (programId != 0) {
            glDeleteProgram(programId)
        }
    }

}