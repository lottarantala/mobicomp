plugins {
    id ("com.android.application")
    id("com.google.dagger.hilt.android")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.example.mobicomp"
    compileSdk = sdk.compile

    defaultConfig {
        applicationId = "com.example.mobicomp"
        minSdk = sdk.min
        targetSdk = sdk.target
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles (
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":core-database"))
    implementation(project(":core-data"))
    implementation(project(":core-domain"))

    implementation(androidx.core.ktx)
    implementation(androidx.lifecycle.compose)
    implementation(androidx.lifecycle.runtime)
    implementation(androidx.activity.compose)
    implementation(androidx.navigation.compose)
    implementation(androidx.compose.ui.ui)
    implementation(androidx.compose.ui.tooling)
    implementation(androidx.compose.material)
    implementation(androidx.room.ktx)
    implementation(androidx.room.common)
    implementation(androidx.room.runtime)
    implementation(androidx.biometric.biometric)
    implementation(androidx.work.ktx)

    implementation("com.google.dagger:hilt-android:2.44.2")
    implementation(androidx.navigation.hilt.compose)
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.1.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.1.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.1.1")

    // Accompanist
    implementation ("com.google.accompanist:accompanist-insets:0.21.0-beta")

    // Navigation
    implementation ("androidx.navigation:navigation-compose:2.4.0-rc01")

    // ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    // ConstraintLayout
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.0-rc02")

    // Foundation
    implementation ("androidx.compose.foundation:foundation:1.1.0-rc01")

    // Maps dependencies
    implementation("com.google.maps.android:maps-ktx:3.3.0")
    implementation("com.google.maps.android:maps-utils-ktx:3.3.0")
    implementation("com.google.android.gms:play-services-maps:18.0.0")
    implementation("com.google.maps.android:android-maps-utils:2.2.3")

    implementation("com.google.android.gms:play-services-location:18.0.0")
}