package utility

class Timer {
    private var lastLoopTime: Double = getTime()

    private fun getTime(): Double {
        return System.nanoTime() / 1_000_000_000.0
    }

    fun getElapsedTime(): Float {
        val time = getTime()
        val elapsedTime = time - lastLoopTime
        lastLoopTime = time
        return elapsedTime.toFloat()
    }

    fun getLastLoopTime(): Double {
        return lastLoopTime
    }
}