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
        versionCode = 8
        versionName = version.toString()
    }
    androidResources {
        generateLocaleConfig = true
    }
    signingConfigs {
        listOf("debug", "release").forEach { configName ->
            val capitalizedName = configName.replaceFirstChar { it.uppercase() }
            val storeFilePath = (project.findProperty("androidSigning${capitalizedName}StoreFile")
                ?: project.findProperty("androidSigningStoreFile")) as String?
            if (!storeFilePath.isNullOrEmpty()) {
                maybeCreate(configName).apply {
                    storeFile = file(storeFilePath)
                    storePassword = (project.findProperty("androidSigning${capitalizedName}StorePassword")
                        ?: project.findProperty("androidSigningStorePassword")) as String?
                    keyAlias = (project.findProperty("androidSigning${capitalizedName}KeyAlias")
                        ?: project.findProperty("androidSigningKeyAlias")) as String?
                    keyPassword = (project.findProperty("androidSigning${capitalizedName}KeyPassword")
                        ?: project.findProperty("androidSigningKeyPassword")) as String?
                }
            } else {
                logger.warn("Store file is not found.(androidSigning${capitalizedName}StoreFile or androidSigningStoreFile)")
            }
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
