package uuid

import kotlinx.benchmark.*
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

typealias JavaUUID = java.util.UUID

@State(Scope.Benchmark)
@Measurement(iterations = 7, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Warmup(iterations = 7, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
open class JavaFormatBench {
    private val size = 1000
    private val javaUUIDs = mutableListOf<JavaUUID>()
    private val uuidStrings = mutableListOf<String>()

    @OptIn(ExperimentalUuidApi::class)
    @Setup
    fun setup() {
        javaUUIDs.clear()
        uuidStrings.clear()

        repeat(size) {
            val msb = Random.nextLong()
            val lsb = Random.nextLong()
            val uuid = Uuid.fromLongs(msb, lsb)
            javaUUIDs.add(JavaUUID(msb, lsb))
            uuidStrings.add(uuid.toString())
        }
    }

    @Benchmark
    fun javaToString(bh: Blackhole) {
        for (uuid in javaUUIDs) {
            bh.consume(uuid.toString())
        }
    }

    @Benchmark
    fun javaParse(bh: Blackhole) {
        for (uuidString in uuidStrings) {
            bh.consume(JavaUUID.fromString(uuidString))
        }
    }

    @Benchmark
    fun javaRandom(bh: Blackhole) {
        repeat(size) {
            bh.consume(JavaUUID.randomUUID())
        }
    }
}