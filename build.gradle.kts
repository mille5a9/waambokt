import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
}

group = "waambokt"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_19

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")

    implementation("com.kotlindiscord.kord.extensions:kord-extensions:1.6.0")
    implementation("it.skrape:skrapeit:1.3.0-alpha.2")
    implementation("io.github.mille5a9:api-nba-kotlin:1.0.4-alpha")

    // Logging dependencies
    implementation("org.apache.groovy:groovy:4.0.17")
    implementation("org.fusesource.jansi:jansi:2.4.0")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
}
application {
    mainClass.set("waambokt.AppKt")
}

tasks.withType<KotlinCompile> {
    // Current LTS version of Java
    kotlinOptions.jvmTarget = "19"
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
