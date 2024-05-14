plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.10"
}

group = "uuid"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.10")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

benchmark {
    configurations {
        register("byteBuffer") {
            include("ByteBufferBench")
        }
        register("format") {
            include("FormatBench.*ToString")
        }
    }
    targets {
        register("main")
    }
}