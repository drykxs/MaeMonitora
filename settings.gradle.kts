pluginManagement {
    repositories {
        gradlePluginPortal()
        google() { metadataSources { mavenPom(); artifact(); gradleMetadata() } }
        mavenCentral() { metadataSources { mavenPom(); artifact(); gradleMetadata() } }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.android.application") {
                useModule("com.android.tools.build:gradle:8.1.0")
            }
        }
    }
}
rootProject.name = "Mamae"
include(":app")