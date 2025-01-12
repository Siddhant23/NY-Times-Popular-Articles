// Top-level build file where you can add configuration options common to all sub-projects/modules.

allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
