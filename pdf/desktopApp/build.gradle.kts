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
            throw GradleException("Conveyor config not found: ${confFile.path}")
        }

        val outDir = outputDir.get().asFile
        outDir.deleteRecursively()
        outDir.mkdirs()

        var isTargetSection = false
        val triggerComment = "// Inputs from dependency configurations and the JAR task."

        // 正規表現の解説:
        // 1. (?:"([^"]+)"|([^\s"\[\],]+)) :
        //    - "で囲まれた中身 (グループ1) OR 引用符なしの文字列 (グループ2)
        // 2. (?:\s*->\s*(?:(?:"([^"]+)")|([^\s"\[\],]+)))? :
        //    - オプションで "->" の後に続く 引用符あり (グループ3) OR 引用符なし (グループ4) のエイリアス
        val regex = """(?:"([^"]+)"|([^\s"\[\],]+))(?:\s*->\s*(?:(?:"([^"]+)")|([^\s"\[\],]+)))?""".toRegex()

        confFile.useLines { lines ->
            lines.forEach { line ->
                val trimmed = line.trim()

                // セクションの開始判定
                if (!isTargetSection) {
                    if (trimmed == triggerComment) isTargetSection = true
                    return@forEach
                }

                // セクション内での不要な行（コメント、空行、ブロック記号のみ）をスキップ
                if (trimmed.isEmpty() || trimmed.startsWith("//") || trimmed == "[" || trimmed == "]") {
                    return@forEach
                }

                // app.inputs += などの接頭辞を除去して解析しやすくする
                val cleanLine = trimmed
                    .replaceFirst("app.inputs\\s*\\+?=\\s*\\$\\{app\\.inputs\\}\\s*\\[".toRegex(), "")
                    .replaceFirst("app.inputs\\s*\\+?=\\s*".toRegex(), "")
                    .trim()

                val match = regex.find(cleanLine)
                if (match != null) {
                    // グループ1(引用符あり)かグループ2(引用符なし)からパスを取得
                    val rawSrcPath = match.groups[1]?.value ?: match.groups[2]?.value ?: return@forEach
                    val srcPath = rawSrcPath.replace("\\\\", "\\") // Windowsエスケープ対策

                    // グループ3(引用符ありエイリアス)かグループ4(引用符なしエイリアス)からエイリアス名を取得
                    val aliasName = match.groups[3]?.value ?: match.groups[4]?.value

                    val srcFile = File(srcPath)
                    if (srcFile.exists()) {
                        val targetFileName = aliasName?.trim() ?: srcFile.name
                        logger.lifecycle("Copying: ${srcFile.name} -> $targetFileName")

                        fileSystemOperations.copy {
                            from(srcFile)
                            into(outDir)
                            rename { targetFileName }
                            duplicatesStrategy = DuplicatesStrategy.INCLUDE
                        }
                    } else {
                        // GitHub Actions上では絶対パスが異なる場合があるためのログ
                        logger.warn("File not found: $srcPath")
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

    dependsOn("writeConveyorConfig", "jvmJar")

    configFile.set(layout.projectDirectory.file("generated.conveyor.conf"))
    outputDir.set(layout.buildDirectory.dir("all-libs"))
}
