plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
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

afterEvaluate {
    publishing {

        publications {
            // Creates a Maven publication called "release".
            release(MavenPublication) {
                // Applies the component for the release build variant.
                // from components.release // 表示发布 release（jitpack 都不会使用到）

                // You can then customize attributes of the publication as shown below.
                groupId = 'com.shukesmart.maplibray.utils' // 这个是依赖库的组 id
                artifactId = 'maplibray' // 依赖库的名称（jitpack 都不会使用到）
                version = '1.0.1'
            }
        }
//        repositories {
//            // 下面这部分，不是很清楚加不加，但是最后加上
//            maven {
//                // change URLs to point to your repos, e.g. http://my.org/repo
//                def baseUrl = buildDir.getParent()
//                def releasesRepoUrl = "$baseUrl/repos/releases"
//                def snapshotsRepoUrl = "$baseUrl/repos/snapshots"
//                url = version.endsWith('SNAPSHOT') ? snapshotsRepoUrl : releasesRepoUrl
//            }
//        }
    }
}