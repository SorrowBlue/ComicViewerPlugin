import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
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
    jvm()

    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }

    sourceSets {
        jvmMain {
            dependencies {
                implementation(projects.pdf)
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
            }
        }
    }
}
compose.desktop {
    application {
        mainClass = "com.sorrowblue.comicviewer.plugin.pdf.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Msi,
                TargetFormat.Deb
            )
            vendor = "SorrowBlue"
            packageVersion = parseVersionForDesktop(version.toString())
            outputBaseDir = file("ComicViewerPDF")
            linux {
                packageName = "comicviewerpdf"
                iconFile = File("icon/linux/appIcon.png")
                installationPath = "ComicViewerPDF"
                debMaintainer = "sorrowblue.dev@gmail.com"
                menuGroup = "sorrowblue-comicViewer"
            }
            windows {
                packageName = "ComicViewer-PDF-plugin"
                iconFile = File("icon/windows/appIcon.ico")
                installationPath = "ComicViewerPDF"
                dirChooser = true
                menuGroup = "ComicViewer"
                upgradeUuid = "F5DB26A2-175B-446C-9EDA-50ACACCB6F8E"
                shortcut = true
            }
        }
        jvmArgs("-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8")
    }
}

private fun parseVersionForDesktop(version: String): String {
    val regex = Regex("""^(\d+)\.(\d+)\.(\d+)""")
    val match = regex.find(version) ?: return version
    val (major, minor, patch) = match.destructured
    val newPatch = patch.toInt() + 1
    return "${major}.${minor}.${newPatch}".also {
        logger.lifecycle("Desktop version: $version -> $it")
    }
}
