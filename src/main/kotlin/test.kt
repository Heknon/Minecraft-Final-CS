import utility.modulo
import world.World

fun main() {
    val x = -52
    val z = -45
    getChunkIndex(x, z)
}

private fun getChunkIndex(x: Int, z: Int) {
    var x_ = x
    var z_ = z
    if (x < 0) {
        x_ -= 16
    }
    if (z < 0) {
        z_ -= 17
    }

    println("x: ${x_ - x_ % 16}")
    println("z: ${z_ - z_ % 16}")

}