import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    `maven-publish`
    `java-library`
}

allprojects {
    group = "com.heroslender"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.gradle.maven-publish")
    }

    repositories {
        maven("https://nexus.heroslender.com/repository/maven-public/")
    }

    dependencies {
        compileOnly(kotlin("stdlib-jdk8"))
        // Custom minimized fastutil dep made using `./find-deps.sh`
        implementation("it.unimi.dsi:fastutil-min:8.5.2-HMF")

        testImplementation(platform("org.junit:junit-bom:5.7.1"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    tasks {
        test{
            useJUnitPlatform()
        }

        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    publishing {
        repositories {
            maven {
                val target = if (project.version.toString().endsWith("-SNAPSHOT"))
                    "https://nexus.heroslender.com/repository/maven-snapshots/"
                else
                    "https://nexus.heroslender.com/repository/maven-releases/"

                name = "heroslender-nexus"
                url = uri(target)

                credentials {
                    username = project.findProperty("nexus.user") as? String
                        ?: System.getenv("NEXUS_USERNAME")
                    password = project.findProperty("nexus.key") as? String
                        ?: System.getenv("NEXUS_PASSWORD")
                }
            }
        }

        publications {
            create<MavenPublication>("maven") {
                artifactId = project.path.replace(":", "hmf-").removeSuffix("-").toLowerCase()
                groupId = project.group.toString()
                version = project.version.toString()

                from(components["java"])

                pom {
                    name.set(project.name)
                    description.set(project.description)
                    url.set("https://github.com/heroslender/menu-framework")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://github.com/heroslender/menu-framework/blob/main/LICENSE")
                        }
                    }

                    developers {
                        developer {
                            id.set("heroslender")
                            name.set("Bruno Martins")
                            email.set("admin@heroslender.com")
                        }
                    }

                    scm {
                        connection.set("https://github.com/heroslender/menu-framework.git")
                        developerConnection.set("git@github.com:heroslender/menu-framework.git")
                        url.set("https://github.com/heroslender/menu-framework")
                    }
                }
            }
        }
    }
}
