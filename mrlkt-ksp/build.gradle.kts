

plugins {
    kotlin("multiplatform")
}


kotlin {
    jvm()
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":mrlkt-annotations"))
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")
                implementation("com.google.devtools.ksp:symbol-processing-api:1.7.20-1.0.7")
            }
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }
    }
}