package uuid

import kotlinx.benchmark.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.random.Random

@State(Scope.Benchmark)
@Measurement(iterations = 7, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Warmup(iterations = 7, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
open class ByteBufferBench {
    private val size = 1000
    private val buffer = ByteBuffer.allocate(size * 16)

    @Setup
    fun setup() {
        Random.nextBytes(buffer.array())
    }

    @Benchmark
    fun getUUID_Long_BE(bh: Blackhole) {
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.position(0)

        repeat(size) {
            bh.consume(buffer.getUUID_Long())
        }
    }

    @Benchmark
    fun getUUID_ByteArray_BE(bh: Blackhole) {
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.position(0)

        repeat(size) {
            bh.consume(buffer.getUUID_ByteArray())
        }
    }

    @Benchmark
    fun getUUID_Long_LE(bh: Blackhole) {
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.position(0)

        repeat(size) {
            bh.consume(buffer.getUUID_Long())
        }
    }

    @Benchmark
    fun getUUID_ByteArray_LE(bh: Blackhole) {
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.position(0)

        repeat(size) {
            bh.consume(buffer.getUUID_ByteArray())
        }
    }
}
