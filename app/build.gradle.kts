import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("kotlinx-serialization")
//    id("com.google.devtools.ksp")

}

android {
    namespace = "com.practicum.playlistmaker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.practicum.playlistmaker"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

//    configurations { implementation.get().exclude(mapOf("group" to "org.jetbrains", "module" to "annotations")) }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

//    implementation(libs.moxy)
//    implementation(libs.kapt)
//    implementation(libs.moxy.compiler)
    implementation("androidx.compose.material:material-icons-core:1.7.8")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation(libs.androidx.navigation.compose)
    implementation (libs.activity.compose)
    implementation(libs.androidx.room)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.runtime.livedata)
//    implementation(libs.androidx.ui.tooling.preview)
//    debugImplementation(libs.androidx.ui.tooling)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.android.peko)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.androidx.fragment)
    implementation(libs.andtoidx.viewPager)
    implementation(libs.koin)
    implementation(libs.kotlinx.serialization)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.gson)
    implementation (libs.retrofit)
    implementation (libs.retrofit2.converter.gson)
    implementation (libs.material)
    implementation (libs.glide)
    annotationProcessor (libs.glide.compiler)
}