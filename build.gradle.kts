plugins {
    val kotlinVersion = "1.7.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.13.2"
}

group = "top.jie65535.jcf"
version = "1.1.0"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
val ktorVersion = "2.2.2"

dependencies {
//    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
}