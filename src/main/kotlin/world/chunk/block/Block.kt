package world.chunk.block

class Block(
    val x: Double,
    val y: Double,
    val z: Double,
    val data: BlockData
) {
    override fun toString(): String {
        return "Block($x, $y, $z)"
    }
}