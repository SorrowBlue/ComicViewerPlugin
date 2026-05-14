plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.comicviewer.detekt)
    alias(libs.plugins.comicviewer.gitTagVersion)
    alias(libs.plugins.comicviewer.license)
    alias(libs.plugins.comicviewer.lint)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.metro)
}

kotlin {
    android {
        namespace = "com.sorrowblue.comicviewer.plugin.pdf"
        androidResources.enable = true
    }
    jvm()

    sourceSets {
        androidMain {
            dependencies {
                implementation(projects.pdf.android)
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
                api(libs.jetbrains.navigation3.ui)
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.aboutlibraries.composeM3)
            }
        }
    }
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

compose.resources {
    publicResClass = true
}

dependencies {
    add("androidRuntimeClasspath", libs.compose.uiTooling)
}

buildConfig {
    packageName = "com.sorrowblue.comicviewer.plugin.pdf"
    buildConfigField("VERSION_NAME", version.toString())
    buildConfigField("TIMESTAMP", System.currentTimeMillis())
}
