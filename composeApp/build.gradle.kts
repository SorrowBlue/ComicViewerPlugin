import com.android.build.api.variant.VariantOutput
import java.io.ByteArrayOutputStream
import kotlin.collections.forEach
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.detektPlugin)
    id("com.mikepenz.aboutlibraries.plugin") version "12.2.3"
}

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
    androidTarget()

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("com.sorrowblue.mupdf:mupdf-kmp-android:0.0.1-SNAPSHOT")
            implementation("com.mikepenz:aboutlibraries-compose-m3:12.2.3")
            implementation("androidx.navigation:navigation-compose:2.9.0")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.pdf"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer.pdf"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
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
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.findByName(name)
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName(name)
        }
    }
    buildFeatures {
        aidl = true
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.sorrowblue.comicviewer.pdf.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.sorrowblue.comicviewer.pdf"
            packageVersion = "1.0.0"
        }
        jvmArgs("-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8")
    }
}
//val gitTagProvider = providers.of(GitTagValueSource::class) {}

androidComponents {
    onVariants(selector().all()) { variant ->
        variant.outputs.forEach { output: VariantOutput ->
//            val vn = gitTagProvider.orElse("0.0.0").get()
//            output.versionName.set(vn)
//            logger.lifecycle("${variant.name} versionName=$vn")
        }
    }
}
