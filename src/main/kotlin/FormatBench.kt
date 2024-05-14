package uuid

import kotlinx.benchmark.*
import kotlin.random.Random

typealias JavaUUID = java.util.UUID

@State(Scope.Benchmark)
@Measurement(iterations = 7, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Warmup(iterations = 7, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
open class FormatBench {
    private val size = 1000
    private val kotlinUUIDs = mutableListOf<UUID>()
    private val javaUUIDs = mutableListOf<JavaUUID>()
    private val uuidStrings = mutableListOf<String>()
    private val uuidHexStrings = mutableListOf<String>()

    @Setup
    fun setup() {
        kotlinUUIDs.clear()
        javaUUIDs.clear()

        repeat(size) {
            val msb = Random.nextLong()
            val lsb = Random.nextLong()
            val uuid = UUID.fromLongs(msb, lsb)
            kotlinUUIDs.add(uuid)
            javaUUIDs.add(JavaUUID(msb, lsb))
            uuidStrings.add(uuid.toString())
            uuidHexStrings.add(uuid.toHexString())
        }
    }

    @Benchmark
    fun kotlinToString(bh: Blackhole) {
        for (uuid in kotlinUUIDs) {
            bh.consume(uuid.toString())
        }
    }

    @Benchmark
    fun javaToString(bh: Blackhole) {
        for (uuid in javaUUIDs) {
            bh.consume(uuid.toString())
        }
    }

    @Benchmark
    fun kotlinToHexString(bh: Blackhole) {
        for (uuid in kotlinUUIDs) {
            bh.consume(uuid.toHexString())
        }
    }

    @Benchmark
    fun kotlinToByteArray(bh: Blackhole) {
        for (uuid in kotlinUUIDs) {
            bh.consume(uuid.toByteArray())
        }
    }

    @Benchmark
    fun kotlinParse(bh: Blackhole) {
        for (uuidString in uuidStrings) {
            bh.consume(UUID.parse(uuidString))
        }
    }

    @Benchmark
    fun javaParse(bh: Blackhole) {
        for (uuidString in uuidStrings) {
            bh.consume(JavaUUID.fromString(uuidString))
        }
    }

    @Benchmark
    fun kotlinParseHex(bh: Blackhole) {
        for (uuidString in uuidHexStrings) {
            bh.consume(UUID.parseHex(uuidString))
        }
    }

    @Benchmark
    fun kotlinRandom(bh: Blackhole) {
        repeat(size) {
            bh.consume(UUID.random())
        }
    }

    @Benchmark
    fun javaRandom(bh: Blackhole) {
        repeat(size) {
            bh.consume(JavaUUID.randomUUID())
        }
    }
}