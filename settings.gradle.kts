pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "MyRobotLab_Mobile"
include(":MyRobotLab_Android")
include(":mrlkt-ksp")
include(":mrlkt")

include(":mrlkt-annotations")
include(":mrlkt-ksp")
