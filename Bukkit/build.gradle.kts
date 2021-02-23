repositories {
    maven("https://repo.codemc.io/repository/nms/")
}

// Workaround to support multiple mc versions
val v1_12: Configuration by configurations.creating { extendsFrom(configurations.compileOnly.get()) }
val v1_8: Configuration by configurations.creating { extendsFrom(configurations.compileOnly.get()) }

dependencies {
    v1_12("org.spigotmc:spigot:1.12.2-R0.1-SNAPSHOT")
    v1_8("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
}

sourceSets {
    main {
        compileClasspath += v1_8 + v1_12
    }
}
