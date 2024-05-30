plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.shukesmart.maplibray"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 33
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.maps.android:android-maps-utils:3.8.2")
    implementation("com.microsoft.cognitiveservices.speech:client-sdk:1.34.0")
    implementation("org.greenrobot:eventbus:3.2.0")

}