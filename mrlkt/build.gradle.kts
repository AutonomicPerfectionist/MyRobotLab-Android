

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("com.google.devtools.ksp") version "1.7.20-1.0.7"
}

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
//    jvm()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../MyRobotLab_iOS/Podfile")
        framework {
            baseName = "mrlkt"
        }
    }

    sourceSets {
        val ktorVersion = "2.1.2"
        val jacksonVersion = "2.13.4"
        val koinVersion= "3.2.2"

        val commonMain by getting {
            kotlin.srcDir(file("build/generated/ksp/android/androidRelease/kotlin"))
            dependencies {
                implementation(project(":mrlkt-annotations"))
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-websockets:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                // Koin
                implementation("io.insert-koin:koin-core:$koinVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }


        val androidMain by getting {
//            kotlin.srcDir(file("build/generated/ksp/android/androidRelease/kotlin"))
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")
                implementation ("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
                implementation ("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
                implementation ("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
                implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
                implementation ("com.fasterxml.jackson.module:jackson-module-no-ctor-deser:$jacksonVersion")
                implementation ("org.reflections:reflections:0.10.2")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }
        val androidTest by getting {
//            kotlin.srcDir(file("build/generated/ksp/android/androidDebug/kotlin"))
            dependencies {

            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "org.myrobotlab"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
}
dependencies {
    //    implementation(project(mapOf("path" to ":mrlkt-annotations")))
    add("kspAndroid", project(":mrlkt-ksp"))
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.13.2")
}

tasks.withType<Test> {
    this.testLogging {
        this.showStandardStreams = true
    }
}
