plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.vibecoding.dailytasks"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vibecoding.dailytasks"
        minSdk = 26
        targetSdk = 34
        versionCode = 11
        versionName = "1.8"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
        viewBinding = true
        buildConfig = true
    }
}

afterEvaluate {
    tasks.named("assembleDebug") {
        doLast {
            val versionName = android.defaultConfig.versionName
            val apk = layout.buildDirectory.file("outputs/apk/debug/app-debug.apk").get().asFile
            val releasesDir = rootProject.layout.projectDirectory.dir("releases").asFile
            releasesDir.mkdirs()
            val dest = releasesDir.resolve("daily-note-v${versionName}-debug.apk")
            apk.copyTo(dest, overwrite = true)
            logger.lifecycle("APK copied to ${dest.absolutePath}")
        }
    }
}
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
}
