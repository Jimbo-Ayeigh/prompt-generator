plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.pricelens.glasses"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pricelens.glasses"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Pricing API key is supplied as a Gradle property (e.g. in ~/.gradle/gradle.properties)
        // so it never lands in source control. Falls back to empty -> pricing shows "set key".
        buildConfigField(
            "String",
            "SERPAPI_KEY",
            "\"${(project.findProperty("serpApiKey") as String? ?: "")}\"",
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")

    // Compose BoM keeps the core Compose artifacts on a single, compatible version.
    val composeBom = platform("androidx.compose:compose-bom:2024.09.02")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material-icons-extended")

    // ViewModel + lifecycle-aware state collection, and coroutines for the scan pipeline.
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // CameraX: live camera feed + per-frame analysis for recognition.
    implementation("androidx.camera:camera-core:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.concurrent:concurrent-futures-ktx:1.2.0")

    // On-device object recognition (image labeling).
    implementation("com.google.mlkit:image-labeling:17.0.9")

    // Android XR runtime + Jetpack Compose Glimmer — the AI Glasses UI toolkit.
    implementation("androidx.xr.runtime:runtime:1.0.0-alpha14")
    implementation("androidx.xr.glimmer:glimmer:1.0.0-alpha12")
    implementation("androidx.xr.glimmer:glimmer-google-fonts:1.0.0-alpha12")
    implementation("androidx.xr.projected:projected:1.0.0-alpha07")
    implementation("androidx.xr.arcore:arcore:1.0.0-alpha13")
}
