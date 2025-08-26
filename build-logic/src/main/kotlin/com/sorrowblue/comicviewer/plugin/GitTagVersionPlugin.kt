package com.sorrowblue.comicviewer.plugin

import java.io.ByteArrayOutputStream
import javax.inject.Inject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.kotlin.dsl.of
import org.gradle.plugin.use.PluginDependency
import org.gradle.process.ExecOperations

@Suppress("unused")
internal class GitTagVersionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val gitTagProvider = providers.of(GitTagValueSource::class) {}
            runCatching {
                val tag = checkNotNull(gitTagProvider.orNull) { "Warning: No git tag found." }
                version =
                    checkNotNull(tag.removePrefix("v")) { "Warning: git tag is not valid." }
            }.onFailure {
                logger.warn("Failed to get git tag. Using version 'UNKNOWN'.")
                version = "0.0.0"
            }
        }
    }

    private fun PluginManager.hasPlugin(provider: Provider<PluginDependency>): Boolean {
        return hasPlugin(provider.get().pluginId)
    }
}

// パラメータは不要だが、インターフェースとして定義が必要
private interface GitTagParameters : ValueSourceParameters

// Gitコマンドを実行して最新タグを取得するValueSource
private abstract class GitTagValueSource @Inject constructor(
    private val execOperations: ExecOperations,
) : ValueSource<String, GitTagParameters> {

    override fun obtain(): String {
        return try {
            // 標準出力をキャプチャするためのByteArrayOutputStream
            val stdout = ByteArrayOutputStream()
            // git describe コマンドを実行
            val result = execOperations.exec {
                // commandLine("git", "tag", "--sort=-creatordate") // もし作成日時順の最新タグが良い場合
                commandLine("git", "describe", "--tags", "--abbrev=1")
                standardOutput = stdout
                // エラーが発生してもGradleビルドを止めないようにし、戻り値で判断
                isIgnoreExitValue = true
                // エラー出力は捨てる (必要ならキャプチャも可能)
                errorOutput = ByteArrayOutputStream()
            }

            if (result.exitValue == 0) {
                // 成功したら標準出力をトリムして返す
                stdout.toString().trim().removePrefix("v")
            } else {
                // gitコマンド失敗時 (タグがない、gitリポジトリでない等)
                println("Warning: Could not get git tag. (Exit code: ${result.exitValue})")
                "v0.0.0-beta.1"
            }
        } catch (e: Exception) {
            // その他の予期せぬエラー
            println("Error: Failed to execute git command: ${e.message}")
            "v0.0.0-beta.1"
        }
    }
}
