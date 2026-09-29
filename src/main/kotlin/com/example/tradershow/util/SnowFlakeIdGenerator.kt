package com.example.tradershow.util



import org.springframework.stereotype.Component

@Component
class SnowflakeIdGenerator {

    companion object {
        private const val EPOCH = 1704067200000L

        private const val WORKER_ID_BITS = 10
        private const val SEQUENCE_BITS = 12

        private const val MAX_WORKER_ID = (1L shl WORKER_ID_BITS) - 1
        private const val MAX_SEQUENCE = (1L shl SEQUENCE_BITS) - 1

        private const val WORKER_ID_SHIFT = SEQUENCE_BITS
        private const val TIMESTAMP_SHIFT =
            SEQUENCE_BITS + WORKER_ID_BITS
    }

    private val workerId = 1L

    private var lastTimestamp = -1L
    private var sequence = 0L

    @Synchronized
    fun nextId(): Long {

        var timestamp = System.currentTimeMillis()

        if (timestamp < lastTimestamp) {
            throw IllegalStateException(
                "Clock moved backwards"
            )
        }

        if (timestamp == lastTimestamp) {

            sequence = (sequence + 1) and MAX_SEQUENCE

            if (sequence == 0L) {
                timestamp = waitForNextMillis(timestamp)
            }

        } else {
            sequence = 0L
        }

        lastTimestamp = timestamp

        return ((timestamp - EPOCH) shl TIMESTAMP_SHIFT) or
                (workerId shl WORKER_ID_SHIFT) or
                sequence
    }

    private fun waitForNextMillis(lastTimestamp: Long): Long {
        var timestamp = System.currentTimeMillis()

        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis()
        }

        return timestamp
    }
}