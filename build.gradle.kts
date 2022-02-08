plugins {
    kotlin("jvm") version "1.6.10"
}

group = "com.IceCreamQAQ.YuQ"
version = "0.1.0.0-DEV1"

repositories {
    mavenLocal()
    maven("https://maven.icecreamqaq.com/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    api("com.IceCreamQAQ:YuQ:0.1.0.0-DEV26")
}