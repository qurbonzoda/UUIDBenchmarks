package uuid

import kotlinx.benchmark.*


@State(Scope.Benchmark)
@Measurement(iterations = 10, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Warmup(iterations = 10, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
class RandomUuidBench {
    @Benchmark
    fun randomUuid(): UUID {
        return secureRandomUuid()
    }

    @Benchmark
    fun randomUuid_getRandomValues(): UUID {
        return secureRandomUUID_getRandomValues()
    }
}
