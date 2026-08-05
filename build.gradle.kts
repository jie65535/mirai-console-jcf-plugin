plugins {
    val kotlinVersion = "2.0.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.16.0"
}

group = "top.jie65535.jcf"
version = "1.2.1"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
val ktorVersion = "3.0.3"

dependencies {
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
}