package com.sorrowblue.comicviewer.plugin.primitive

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import com.sorrowblue.comicviewer.plugin.libs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

when {
    pluginManager.hasPlugin(libs.plugins.androidApplication) -> {
        configure<ApplicationExtension> {
            lint {
                configure()
            }
        }
    }

    pluginManager.hasPlugin(libs.plugins.androidLibrary) -> {
        configure<LibraryExtension> {
            lint {
                configure()
            }
        }
    }

    pluginManager.hasPlugin(libs.plugins.androidMultiplatformLibrary) -> {
        configure<KotlinMultiplatformExtension> {
            configure<KotlinMultiplatformAndroidLibraryExtension> {
                lint {
                    configure()
                }
            }
        }
    }
}

private fun Lint.configure() {
    val isCI = System.getenv("CI").toBoolean()
    checkAllWarnings = true
    checkDependencies = true
    baseline = project.rootProject.file("config/lint-baseline.xml")
    lintConfig = project.rootProject.file("config/lint.xml")
    htmlReport = !isCI
    htmlOutput =
        if (htmlReport) {
            project.file("${project.rootDir}/build/reports/lint/lint-result.html")
        } else {
            null
        }
    sarifReport = isCI
    sarifOutput =
        if (sarifReport) {
            project.file("${project.rootDir}/build/reports/lint/lint-result.sarif")
        } else {
            null
        }
    textReport = false
    xmlReport = false
}

private fun PluginManager.hasPlugin(provider: Provider<PluginDependency>): Boolean =
    hasPlugin(provider.get().pluginId)
