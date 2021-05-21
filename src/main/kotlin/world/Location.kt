package world

data class Location(val world: World?, val x: Double, val y: Double, val z: Double) {
    fun compress(): Long {
        return x.toLong() and 0x7FFFFFF or (z.toLong() and 0x7FFFFFF shl 27) or (y.toLong() shl 54)
    }
}

fun Long.getLocation(): Location {
    val packed = this
    val x = (packed shl 37 shr 37).toDouble()
    val y = (packed ushr 54).toDouble()
    val z = (packed shl 10 shr 37).toDouble()
    return Location(null, x, y, z)
}