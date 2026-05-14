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
    alias(libs.plugins.metro)
    alias(libs.plugins.conveyor)
}

kotlin {
    jvm()

    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.componentsResources)
            }
        }
        jvmMain {
            dependencies {
                implementation(projects.pdf)
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
            }
        }
    }
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
            targetFormats(TargetFormat.Exe)
            vendor = "SorrowBlue"
            packageName = "comicviewer-pdf-plugin"
            linux {
                iconFile = File("icon/linux/appIcon.png")
                debMaintainer = "sorrowblue.dev@gmail.com"
                menuGroup = "comicviewer"
            }
            windows {
                iconFile = File("icon/windows/appIcon.ico")
                installationPath = "ComicViewerPdfPlugin"
                dirChooser = true
                menuGroup = "ComicViewer"
                upgradeUuid = "F5DB26A2-175B-446C-9EDA-50ACACCB6F8E"
                shortcut = true
            }
        }
        jvmArgs("-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8")
    }
}

compose.resources {
    packageOfResClass = "comicviewerplugin.pdf.desktop.generated.resources"
}
