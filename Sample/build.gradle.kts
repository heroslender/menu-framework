plugins {
    kotlin("jvm") version "1.8.20"
    id("org.jetbrains.compose") version "1.4.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("com.heroslender:hmf-bukkit:0.1.0-SNAPSHOT")

    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT") {
        exclude("net.md-5", "bungeecord-chat")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

bukkit {
    main = "com.heroslender.hmf.sample.SamplePlugin"
    name = project.name
    version = project.version.toString()
    author = "Heroslender"

    commands {
        create("menu") {
            description = "Command to test menus"
            usage = "/menu"
        }
    }
}
