rootProject.name = "sample"

includeBuild("..") {
    dependencySubstitution {
        substitute(module("com.heroslender:hmf-bukkit")).using(project(":bukkit"))
    }
}
