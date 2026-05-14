plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.comicviewer.detekt)
    alias(libs.plugins.comicviewer.gitTagVersion)
    alias(libs.plugins.comicviewer.license)
    alias(libs.plugins.comicviewer.lint)
    alias(libs.plugins.metro)
}

android {
    namespace = "com.sorrowblue.comicviewer.plugin.pdf.app"
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer.plugin.pdf"
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 5
        versionName = version.toString()
    }
    androidResources {
        generateLocaleConfig = true
    }
    signingConfigs {
        val androidSigningDebugStoreFile: String? by project
        if (!androidSigningDebugStoreFile.isNullOrEmpty()) {
            getByName("debug") {
                val androidSigningDebugStorePassword: String? by project
                val androidSigningDebugKeyAlias: String? by project
                val androidSigningDebugKeyPassword: String? by project
                storeFile = file(androidSigningDebugStoreFile!!)
                storePassword = androidSigningDebugStorePassword
                keyAlias = androidSigningDebugKeyAlias
                keyPassword = androidSigningDebugKeyPassword
            }
        } else {
            logger.warn("androidSigningDebugStoreFile not found")
        }

        val androidSigningReleaseStoreFile: String? by project
        if (!androidSigningReleaseStoreFile.isNullOrEmpty()) {
            create("release") {
                val androidSigningReleaseStorePassword: String? by project
                val androidSigningReleaseKeyAlias: String? by project
                val androidSigningReleaseKeyPassword: String? by project
                storeFile = file(androidSigningReleaseStoreFile!!)
                storePassword = androidSigningReleaseStorePassword
                keyAlias = androidSigningReleaseKeyAlias
                keyPassword = androidSigningReleaseKeyPassword
            }
        } else {
            logger.warn("androidSigningReleaseStoreFile not found")
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            //noinspection NotShrinkingResources
            isShrinkResources = false
            signingConfig = signingConfigs.findByName(name)
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName(name)
            ndk.debugSymbolLevel = "FULL"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

dependencies {
    implementation(projects.pdf)
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity.compose)
}

aboutLibraries {
    export {
        outputFile.set(file("src/main/res/raw/aboutlibraries.json"))
    }
}
