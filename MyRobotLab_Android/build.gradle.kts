plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp") version "1.7.20-1.0.7"
}

android {
    namespace = "org.myrobotlab.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "org.myrobotlab.android"
        minSdk = 22
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

android.sourceSets["main"].java {
    srcDir("$buildDir/generated/ksp/debug/kotlin")
}

dependencies {
    val koinVersion= "3.2.2"
    val koinAndroidVersion= "3.3.0"
    val koinAndroidComposeVersion= "3.3.0"
    val jacksonVersion = "2.13.4"
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.21")
    implementation(project(":mrlkt"))
    implementation ("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("androidx.compose.ui:ui:1.2.1")
    implementation("androidx.compose.ui:ui-tooling:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.2.1")
    implementation("androidx.compose.foundation:foundation:1.2.1")
    implementation("androidx.compose.material:material:1.2.1")
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("androidx.fragment:fragment-ktx:1.6.0-alpha03")
    // Accompanist
    implementation("com.google.accompanist:accompanist-pager:0.25.0") // Pager
    implementation("com.google.accompanist:accompanist-pager-indicators:0.25.0") // Pager Indicators
    implementation("com.google.accompanist:accompanist-webview:0.25.0")
    implementation(project(mapOf("path" to ":mrlkt-annotations")))

    // Koin
    implementation("io.insert-koin:koin-core:$koinVersion")
    // Koin main features for Android
    implementation("io.insert-koin:koin-android:$koinAndroidVersion")
    // Java Compatibility
    implementation("io.insert-koin:koin-android-compat:$koinAndroidVersion")
    // Jetpack Compose
    implementation("io.insert-koin:koin-androidx-compose:$koinAndroidComposeVersion")

    // Better permissions library
    implementation("com.github.fondesa:kpermissions:3.4.0")

    val ktorVersion = "2.1.2"

    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

    implementation(project(":mrlkt-ksp"))

    ksp(project(":mrlkt-ksp"))
}