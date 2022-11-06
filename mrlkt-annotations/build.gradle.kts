plugins {
    kotlin("multiplatform")

//    id("java-library")
//    id("org.jetbrains.kotlin.jvm")
}
kotlin {
    ios()
    iosSimulatorArm64()
    jvm()
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("src/main/java")
        }
    }
}

//java {
//    sourceCompatibility = JavaVersion.VERSION_1_7
//    targetCompatibility = JavaVersion.VERSION_1_7
//}