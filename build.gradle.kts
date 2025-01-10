import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform") version "2.0.21"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.12"
}

group = "uuid"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)

    jvm()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
    }
    js {
        nodejs()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.12")
            }
        }
    }
}

benchmark {
    configurations {
        register("byteBuffer") {
            include("ByteBufferBench")
        }
        register("format") {
            include("FormatBench.*ToString.*")
            include("FormatBench.*Parse")
        }
        register("randomUuid") {
            include("RandomUuidBench")
        }
    }
    targets {
        register("jvm")
        register("wasmJs")
        register("js")
    }
}