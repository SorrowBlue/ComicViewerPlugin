plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.comicviewer.detekt)
    alias(libs.plugins.comicviewer.gitTagVersion)
    alias(libs.plugins.comicviewer.license)
    alias(libs.plugins.comicviewer.lint)
}

android {
    namespace = "com.sorrowblue.comicviewer.plugin.pdf.android"
    defaultConfig {
        buildConfigField("String", "VERSION_NAME", "\"${version}\"")
    }
    buildFeatures {
        aidl = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}
