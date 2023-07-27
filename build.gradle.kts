import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application

    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.gitlab.arturbosch.detekt")
    id("com.github.johnrengelman.shadow")
}

group = "waambokt"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    google()
    mavenCentral()

    maven {
        name = "Sonatype Snapshots (Legacy)"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }

    maven {
        name = "Sonatype Snapshots"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.21.0-RC2")

    implementation("com.kotlindiscord.kord.extensions:kord-extensions:1.5.6-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
    implementation("it.skrape:skrapeit:1.3.0-alpha.1")

    // Logging dependencies
    implementation("org.codehaus.groovy:groovy:3.0.9")
    implementation("org.fusesource.jansi:jansi:2.4.0")
    implementation("ch.qos.logback:logback-classic:1.3.5")
    implementation("io.github.microutils:kotlin-logging:2.1.23")
}
application {
    // This is deprecated, but the Shadow plugin requires it
    mainClassName = "waambokt.AppKt"
}

tasks.withType<KotlinCompile> {
    // Current LTS version of Java
    kotlinOptions.jvmTarget = "17"

    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "waambokt.AppKt"
    }
}

detekt {
    buildUponDefaultConfig = true
    config = rootProject.files("detekt.yml")
}
