import java.io.InputStream
import java.nio.ByteBuffer
import java.util.*

fun String.loadResource(): String {
    return try {
        val inp: InputStream = javaClass.getResourceAsStream(this)
        val scanner = Scanner(inp, java.nio.charset.StandardCharsets.UTF_8.name())
        scanner.useDelimiter("\\A").next()
    } catch (ex: Exception) {
        ""
    }
}

fun String.loadResourceAsByteBuffer(): ByteBuffer {
    val imgBytes = javaClass.getResourceAsStream(this).readBytes()
    val buf = ByteBuffer.wrap(imgBytes)
    println(imgBytes)
    return buf
}