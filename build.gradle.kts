import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform") version "2.0.0"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.11"
}

group = "uuid"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

@OptIn(ExperimentalWasmDsl::class)
kotlin {
    jvmToolchain(17)

    jvm()
    wasmJs {
        nodejs()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.11")
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
            include("FormatBench")
        }
        register("randomUuid") {
            include("RandomUuidBench")
        }
    }
    targets {
        register("jvm")
        register("wasmJs")
    }
}