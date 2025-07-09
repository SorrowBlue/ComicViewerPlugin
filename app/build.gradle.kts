plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.composeCompiler)
    id("com.mikepenz.aboutlibraries.plugin") version "12.2.3"
}

android {
    namespace = "com.sorrowblue.comicviewer.pdf"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer.pdf"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val androidSigningReleaseStoreFile: String? by project
            val androidSigningReleaseStorePassword: String? by project
            val androidSigningReleaseKeyAlias: String? by project
            val androidSigningReleaseKeyPassword: String? by project
            storeFile = file(androidSigningReleaseStoreFile!!)
            storePassword = androidSigningReleaseStorePassword
            keyAlias = androidSigningReleaseKeyAlias
            keyPassword = androidSigningReleaseKeyPassword
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        aidl = true
        compose = true
    }

    kotlin {
        jvmToolchain {
            vendor.set(JvmVendorSpec.ADOPTIUM)
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("com.sorrowblue.mupdf:mupdf-kmp-android:0.0.1-SNAPSHOT")
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("com.mikepenz:aboutlibraries-compose-m3:12.2.3")
    implementation("androidx.navigation:navigation-compose:2.9.0")
}
