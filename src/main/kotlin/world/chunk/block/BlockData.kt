package world.chunk.block

data class BlockData(
    val id: Int,
    val name: String,
    val faces: Map<BlockProvider.Direction, BlockProvider.BlockFaceMesh>,
    val collidable: Boolean,
    val transparent: Boolean
) {

}