plugins {
    kotlin("jvm") version "1.4.21"
    java
    kotlin("kapt") version "1.4.21"
}

group = "com.xsw22wsx"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))
    implementation(project(":typer"))

    implementation("com.squareup:kotlinpoet:1.7.2")
    implementation("com.squareup:kotlinpoet-metadata:1.7.2")
    implementation("com.squareup:kotlinpoet-metadata-specs:1.7.2")


    implementation("com.google.auto.service:auto-service:1.0-rc4")
    kapt("com.google.auto.service:auto-service:1.0-rc4")
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}