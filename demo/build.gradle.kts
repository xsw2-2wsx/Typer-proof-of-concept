plugins {
    java
    kotlin("jvm") version "1.4.21"
    kotlin("kapt") version "1.4.21"
}

group = "com.xsw22wsx"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":typer"))
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    kapt(project(":annotation-processor"))
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

kapt {
    generateStubs = true
}