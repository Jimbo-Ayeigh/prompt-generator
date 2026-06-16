plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.promptarchitect.glasses"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.promptarchitect.glasses"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
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

    // Android XR runtime + Jetpack Compose Glimmer — the AI Glasses UI toolkit.
    implementation("androidx.xr.runtime:runtime:1.0.0-alpha14")
    implementation("androidx.xr.glimmer:glimmer:1.0.0-alpha12")
    implementation("androidx.xr.glimmer:glimmer-google-fonts:1.0.0-alpha12")
    implementation("androidx.xr.projected:projected:1.0.0-alpha07")
    implementation("androidx.xr.arcore:arcore:1.0.0-alpha13")
}
