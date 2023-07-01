plugins {
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    api(project(":core"))
    api(project(":bukkit-sdk"))
}
