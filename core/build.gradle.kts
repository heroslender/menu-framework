import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
//
//plugins {
//    id("org.jetbrains.compose") version "1.4.0"
//}
//
//
//tasks.withType<KotlinCompile> {
//    kotlinOptions {
//        freeCompilerArgs = listOf(
//            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
//        )
////        jvmTarget = "17"
//    }
//}
//
//repositories {
//    google()
//    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
//}
//
//compose{
//    experimental
//}
//
//dependencies {
//    // Shaded
//    api(compose.runtime) {
//        exclude("org.jetbrains.kotlin")
//        exclude("org.jetbrains.kotlinx")
//    }
//}
