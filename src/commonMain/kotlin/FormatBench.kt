package uuid

import kotlinx.benchmark.*
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@State(Scope.Benchmark)
@Measurement(iterations = 7, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Warmup(iterations = 7, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
open class FormatBench {
    private val size = 1000
    private val kotlinUUIDs = mutableListOf<UUID>()
    private val kotlinUuids = mutableListOf<Uuid>()
    private val uuidStrings = mutableListOf<String>()
    private val uuidHexStrings = mutableListOf<String>()
    private val uuidByteArrays = mutableListOf<ByteArray>()

    @Setup
    fun setup() {
        kotlinUUIDs.clear()
        kotlinUuids.clear()
        uuidStrings.clear()
        uuidHexStrings.clear()
        uuidByteArrays.clear()

        repeat(size) {
            val msb = Random.nextLong()
            val lsb = Random.nextLong()
            val uuid = Uuid.fromLongs(msb, lsb)
            kotlinUUIDs.add(UUID.fromLongs(msb, lsb))
            kotlinUuids.add(uuid)
            uuidStrings.add(uuid.toString())
            uuidHexStrings.add(uuid.toHexString())
            uuidByteArrays.add(uuid.toByteArray())
        }
    }

    @Benchmark
    fun optimizedToString(bh: Blackhole) {
        for (uuid in kotlinUUIDs) {
            bh.consume(uuid.toString())
        }
    }

    @Benchmark
    fun originalToString(bh: Blackhole) {
        for (uuid in kotlinUuids) {
            bh.consume(uuid.toString())
        }
    }

    @Benchmark
    fun optimizedToHexString(bh: Blackhole) {
        for (uuid in kotlinUUIDs) {
            bh.consume(uuid.toHexString())
        }
    }

    @Benchmark
    fun originalToHexString(bh: Blackhole) {
        for (uuid in kotlinUuids) {
            bh.consume(uuid.toHexString())
        }
    }

    @Benchmark
    fun optimizedToByteArray(bh: Blackhole) {
        for (uuid in kotlinUUIDs) {
            bh.consume(uuid.toByteArray())
        }
    }

    @Benchmark
    fun originalToByteArray(bh: Blackhole) {
        for (uuid in kotlinUuids) {
            bh.consume(uuid.toByteArray())
        }
    }

    @Benchmark
    fun optimizedParse(bh: Blackhole) {
        for (uuidString in uuidStrings) {
            bh.consume(UUID.parse(uuidString))
        }
    }

    @Benchmark
    fun originalParse(bh: Blackhole) {
        for (uuidString in uuidStrings) {
            bh.consume(Uuid.parse(uuidString))
        }
    }

    @Benchmark
    fun optimizedParseHex(bh: Blackhole) {
        for (hexString in uuidHexStrings) {
            bh.consume(UUID.parseHex(hexString))
        }
    }

    @Benchmark
    fun originalParseHex(bh: Blackhole) {
        for (hexString in uuidHexStrings) {
            bh.consume(Uuid.parseHex(hexString))
        }
    }

    @Benchmark
    fun optimizedFromByteArray(bh: Blackhole) {
        for (byteArray in uuidByteArrays) {
            bh.consume(UUID.fromByteArray(byteArray))
        }
    }

    @Benchmark
    fun originalFromByteArray(bh: Blackhole) {
        for (byteArray in uuidByteArrays) {
            bh.consume(Uuid.fromByteArray(byteArray))
        }
    }

    @Benchmark
    fun optimizedRandom(bh: Blackhole) {
        repeat(size) {
            bh.consume(UUID.random())
        }
    }

    @Benchmark
    fun originalRandom(bh: Blackhole) {
        repeat(size) {
            bh.consume(Uuid.random())
        }
    }
}