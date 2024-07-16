plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "1.5.30"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation("io.ktor:ktor-client-core:1.6.3")
    implementation("io.ktor:ktor-client-cio:1.6.3")
    implementation("io.ktor:ktor-client-logging:1.6.3")
    implementation("io.ktor:ktor-client-serialization:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}