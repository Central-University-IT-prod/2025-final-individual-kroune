@file:OptIn(ExperimentalEncodingApi::class)

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "io.github.kroune.superfinancer"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.kroune.superfinancer"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = "key0"
            // in ci/cd we pass our keystore and keystore password in env variables
            if (System.getenv("KEYSTORE") != null && System.getenv("KEYSTORE_PASSWORD") != null) {
                val file = File.createTempFile("keyStore", ".jks")
                file.writeBytes(Base64.decode(System.getenv("KEYSTORE")!!.toByteArray()))

                storeFile = file
                storePassword = System.getenv("KEYSTORE_PASSWORD")!!
                keyPassword = System.getenv("KEYSTORE_PASSWORD")!!
            } else {
                storeFile = file("keystore.jks")
                storePassword = file("keystore_password").readText().trim()
                keyPassword = file("keystore_password").readText().trim()
            }
        }
    }
    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        managedDevices {
            localDevices {
                create("pixel2api30") {
                    // Use device profiles you typically see in Android Studio.
                    device = "Pixel 2"
                    // Use only API levels 27 and higher.
                    apiLevel = 30
                    // To include Google services, use "google".
                    systemImageSource = "aosp"
                }
            }
        }
    }
}

dependencies {
    // compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)

    // decompose
    implementation(libs.decompose)
    implementation(libs.decompose.jetbrains)

    // encryption
    implementation(libs.androidx.security.crypto)

    // kotlinx serialization
    implementation(libs.kotlinx.serialization.json)

    // ktor
    implementation(libs.ktor.client.cio)
    implementation(libs.koin.core)

    // coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor3)

    // coroutines
    implementation(libs.kotlinx.coroutines.core)

    // paging
    implementation(libs.androidx.paging.compose)

    // room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // our own modules
    implementation(project(":super-financer-ui"))
    implementation(project(":super-financer-api"))

    // testing
    testImplementation(libs.koin.test.junit5)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
