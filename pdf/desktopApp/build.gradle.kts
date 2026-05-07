import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.comicviewer.detekt)
    alias(libs.plugins.comicviewer.gitTagVersion)
    alias(libs.plugins.comicviewer.license)
    alias(libs.plugins.comicviewer.lint)
    alias(libs.plugins.buildconfig)
    id("dev.hydraulic.conveyor") version "2.0"
}

afterEvaluate {
    version = parseVersionForDesktop(version.toString())
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

dependencies {
    linuxAmd64("org.jetbrains.compose.desktop:desktop-jvm-linux-x64:1.10.3")
    macAmd64("org.jetbrains.compose.desktop:desktop-jvm-macos-x64:1.10.3")
    macAarch64("org.jetbrains.compose.desktop:desktop-jvm-macos-arm64:1.10.3")
    windowsAmd64("org.jetbrains.compose.desktop:desktop-jvm-windows-x64:1.10.3")
}

aboutLibraries {
    export {
        outputFile.set(file("src/jvmMain/composeResources/files/aboutlibraries.json"))
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
            packageName = "comicviewer-pdf-plugin"
            packageVersion = parseVersionForDesktop(version.toString())
            linux {
                iconFile = File("icon/linux/appIcon.png")
                debMaintainer = "sorrowblue.dev@gmail.com"
                menuGroup = "sorrowblue-comicViewer"
            }
            windows {
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
