import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import javax.inject.Inject
import org.gradle.api.file.FileSystemOperations

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

abstract class CopyConveyorInputsTask @Inject constructor(
    private val fileSystemOperations: FileSystemOperations,
) : DefaultTask() {

    @get:InputFile
    abstract val configFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        val confFile = configFile.get().asFile
        if (!confFile.exists()) {
            throw GradleException("Conveyor generated config file not found at: ${confFile.path}")
        }

        val outDir = outputDir.get().asFile

        outDir.deleteRecursively()
        outDir.mkdirs()

        var isTargetSection = false
        val triggerComment = "// Inputs from dependency configurations and the JAR task."

        // 1行ずつ解析

        confFile.useLines { lines ->
            lines.forEach { line ->
                val trimmed = line.trim()

                // 1. トリガーとなるコメント行を探す
                if (!isTargetSection) {
                    if (trimmed == triggerComment) {
                        isTargetSection = true
                        logger.lifecycle("Target section started.")
                    }
                    return@forEach // 次の行へ
                }

                // 3. パスとエイリアスの抽出
                // 正規表現: "パス" -> エイリアス (エイリアスは任意)
                val match = "\"?(.+?)\"?(?:\\s*->\\s*([^\\s,]+))?".toRegex().find(trimmed)

                if (match != null) {
                    // HOCON内ではバックスラッシュがエスケープされている場合があるため置換
                    val rawSrcPath = match.groups[1]?.value ?: return@forEach
                    val srcPath = rawSrcPath.replace("\\\\", "\\")

                    val aliasName = match.groups[2]?.value?.trim()

                    val srcFile = File(srcPath)

                    if (srcFile.exists()) {
                        val targetFileName = aliasName ?: srcFile.name
                        logger.lifecycle("Copying: ${srcFile.name} -> $targetFileName")

                        fileSystemOperations.copy {
                            from(srcFile)
                            into(outDir)
                            rename { targetFileName }
                            duplicatesStrategy = DuplicatesStrategy.INCLUDE
                        }
                    } else {
                        // GitHub Actions上でパスが解決できない場合の警告
                        logger.warn("File not found (Skipped): $srcPath")
                    }
                }
            }
        }
        logger.lifecycle("All files copied to: ${outDir.path}")
    }
}

tasks.register<CopyConveyorInputsTask>("copyConveyorInputs") {
    description = "Conveyorのconfigからapp.inputsを解析し、リネームを考慮して特定のディレクトリにコピーします"
    group = "distribution"

    dependsOn("writeConveyorConfig")

    configFile.set(layout.projectDirectory.file("generated.conveyor.conf"))
    outputDir.set(layout.buildDirectory.dir("all-libs"))
}
