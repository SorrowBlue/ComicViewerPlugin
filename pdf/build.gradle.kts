plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.comicviewer.detekt)
    alias(libs.plugins.comicviewer.gitTagVersion)
    alias(libs.plugins.comicviewer.lint)
    alias(libs.plugins.buildconfig)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.plugin.pdf"
        androidResources.enable = true
    }
    jvm("desktop")

    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(projects.pdf.android)
                implementation(libs.androidx.core)
                implementation(libs.androidx.activity.compose)
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("com.mikepenz:aboutlibraries-compose-m3:12.2.3")
                implementation("androidx.navigation:navigation-compose:2.9.8")
            }
        }
        commonMain {
            dependencies {
                implementation(libs.mupdf.kmp)
                implementation(libs.compose.runtime)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.componentsResources)
                implementation(libs.compose.preview)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtimeCompose)
            }
        }
        getByName("desktopMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
            }
        }
    }
}

//android {
//    namespace = "com.sorrowblue.comicviewer.plugin.pdf"
//    compileSdk = libs.versions.android.compileSdk.get().toInt()
//
//    defaultConfig {
//        applicationId = "com.sorrowblue.comicviewer.plugin.pdf"
//        minSdk = libs.versions.android.minSdk.get().toInt()
//        targetSdk = libs.versions.android.targetSdk.get().toInt()
//        versionCode = 4
//        versionName = version.toString()
//    }
//    androidResources {
//        generateLocaleConfig = true
//    }
//    signingConfigs {
//        val androidSigningDebugStoreFile: String? by project
//        if (!androidSigningDebugStoreFile.isNullOrEmpty()) {
//            getByName("debug") {
//                val androidSigningDebugStorePassword: String? by project
//                val androidSigningDebugKeyAlias: String? by project
//                val androidSigningDebugKeyPassword: String? by project
//                storeFile = file(androidSigningDebugStoreFile!!)
//                storePassword = androidSigningDebugStorePassword
//                keyAlias = androidSigningDebugKeyAlias
//                keyPassword = androidSigningDebugKeyPassword
//            }
//        } else {
//            logger.warn("androidSigningDebugStoreFile not found")
//        }
//
//        val androidSigningReleaseStoreFile: String? by project
//        if (!androidSigningReleaseStoreFile.isNullOrEmpty()) {
//            create("release") {
//                val androidSigningReleaseStorePassword: String? by project
//                val androidSigningReleaseKeyAlias: String? by project
//                val androidSigningReleaseKeyPassword: String? by project
//                storeFile = file(androidSigningReleaseStoreFile!!)
//                storePassword = androidSigningReleaseStorePassword
//                keyAlias = androidSigningReleaseKeyAlias
//                keyPassword = androidSigningReleaseKeyPassword
//            }
//        } else {
//            logger.warn("androidSigningReleaseStoreFile not found")
//        }
//    }
//
//    buildTypes {
//        debug {
//            isMinifyEnabled = false
//            isShrinkResources = false
//            signingConfig = signingConfigs.findByName(name)
//        }
//        release {
//            isMinifyEnabled = true
//            isShrinkResources = true
//            signingConfig = signingConfigs.findByName(name)
//            ndk.debugSymbolLevel = "FULL"
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//    buildFeatures {
//        aidl = true
//        buildConfig = true
//    }
//}

dependencies {
    add("androidRuntimeClasspath", libs.compose.uiTooling)
}

buildConfig {
    packageName = "com.sorrowblue.comicviewer.plugin.pdf"
    buildConfigField("VERSION_NAME", version.toString())
    buildConfigField("TIMESTAMP", System.currentTimeMillis())
}

//compose.desktop {
//    application {
//        mainClass = "com.sorrowblue.comicviewer.plugin.pdf.MainKt"
//
//        nativeDistributions {
//            targetFormats(
//                TargetFormat.Msi,
//                TargetFormat.Exe,
//                TargetFormat.Deb
//            )
//            packageName = "ComicViewer-PDF-plugin"
//            vendor =
//                "SorrowBlue"
//            packageVersion = "1.1.1"
//            outputBaseDir = file("ComicViewerPDF")
//            linux {
//                iconFile = File("icon/linux/appIcon.png")
//                installationPath = "ComicViewerPDF"
//                debMaintainer = "sorrowblue.dev@gmail.com"
//                menuGroup = "sorrowblue-comicViewer"
//            }
//            windows {
//                iconFile = File("icon/windows/appIcon.ico")
//                installationPath = "ComicViewerPDF"
//                dirChooser = true
//                menuGroup = "ComicViewer"
//                upgradeUuid = "F5DB26A2-175B-446C-9EDA-50ACACCB6F8E"
//                shortcut = true
//            }
//        }
//        jvmArgs("-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8")
//    }
//}

//androidComponents {
//    onVariants(selector().all()) { variant ->
//        variant.outputs.forEach { output: VariantOutput ->
//            output.versionName.set(version.toString())
//        }
//    }
//}

//private fun parseVersionForDesktop(version: String): String {
//    logger.lifecycle("tag=$version")
//    val regex = Regex("""^(\d+)\.(\d+)\.(\d+)""")
//    val match = regex.find(version) ?: return version.removePrefix("v")
//    val (major, minor, patch) = match.destructured
//    return "${major}.${minor}.${patch}"
//}
