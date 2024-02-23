@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    kotlin("kapt")
    alias(libs.plugins.google.hilt)
}

android {
    namespace = "com.mario.template"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mario.template"
        //applicationId = "com.mario.classytaxi"   // googlePlay LVC : InApp Test
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "retrofit2.pro", "gson.pro", "okhttp3.pro", "firebase-crashlytics.pro"
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
        dataBinding = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // First
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(platform(libs.compose.bom))
    implementation(platform(libs.compose.bom))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // Hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    implementation(libs.google.dagger.hilt.android)
    kapt(libs.google.dagger.hilt.android.compiler)

    // Coroutine
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // AndroidX
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.work)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.livedata)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    kapt(libs.androidx.room.compiler)
//    implementation(libs.androidx.wear.material)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.biometric)

    // ComposeView
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.material.icons.ext)

    // Layout
    implementation(libs.constraintlayout)
    implementation(libs.androidx.drawerlayout)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    implementation(libs.material)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.swiperefreshlayout)

    // View
    implementation(libs.mpchart)
    implementation(libs.circleindicator)

    // Extension
    implementation(libs.bundles.google.accompanist)

    // Network
    implementation(libs.google.gson)
    implementation(libs.bundles.retrofit2)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.coil.image)
    implementation(libs.glide.image)
    annotationProcessor(libs.glide.compiler)
//    implementation(libs.glide.composer)
    implementation(libs.volley)

    // BLE
    implementation(libs.bluetoothspp)
    implementation(libs.fastble)

    // Google
    implementation(libs.google.play.services.base)
    implementation(libs.google.play.services.maps)
    implementation(libs.google.play.services.location)
    implementation(libs.google.maps.utils)

    // Other
    implementation(libs.timber)
    implementation(libs.image.cropper)
    implementation(libs.utilcodex)
    implementation(libs.jsoup)

    // Test
    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.kotlinx.coroutines.test)

    //Custom
    implementation(project(":libMBase"))
    //implementation 'com.github.happymario:template-android:0.0.2'
}