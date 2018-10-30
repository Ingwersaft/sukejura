import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.0-rc-190"
}

group = "io.kesselring.kron"
version = "1.0.0-SNAPSHOT"

repositories {
    maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap") }
    mavenCentral()
    jcenter()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.0-RC1")

    testCompile("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}