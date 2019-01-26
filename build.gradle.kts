import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.intellij") version "0.4.2"
    id("org.jetbrains.kotlin.jvm") version "1.3.20"
}

group = "com.bloidonia"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

@Suppress("UNUSED_VARIABLE")
tasks {
    val compileKotlin by getting(KotlinCompile::class) {
        kotlinOptions.jvmTarget = "1.8"
    }

    val compileTestKotlin by getting(KotlinCompile::class) {
        kotlinOptions.jvmTarget = "1.8"
    }
}

intellij {
    this.version = "2018.3.3"
}
