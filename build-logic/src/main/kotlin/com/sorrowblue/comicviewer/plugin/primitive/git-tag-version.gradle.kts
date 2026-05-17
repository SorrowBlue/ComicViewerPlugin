package com.sorrowblue.comicviewer.plugin.primitive

import java.io.ByteArrayOutputStream

private val gitTagProvider = providers.of(GitTagValueSource::class) {}
version = formatVersion(gitTagProvider.get())

interface GitTagParameters : ValueSourceParameters

@Suppress("AbstractClassCanBeConcreteClass")
abstract class GitTagValueSource @Inject constructor(private val execOperations: ExecOperations) :
    ValueSource<String, GitTagParameters> {

    override fun obtain(): String = try {
        val stdout = ByteArrayOutputStream()
        val stderr = ByteArrayOutputStream()
        val result = execOperations.exec {
            commandLine("git", "describe", "--tags", "--abbrev=1")
            standardOutput = stdout
            errorOutput = stderr
            isIgnoreExitValue = true
        }
        if (result.exitValue == 0) {
            stdout.toString().trim()
        } else {
            println("// Warning: Could not get git tag. (Exit code: ${result.exitValue})")
            "v1.0.0"
        }
    } catch (e: Exception) {
        println("// Error: Failed to execute git command: ${e.message}")
        "v1.0.0"
    }
}

private fun formatVersion(input: String): String {
    if (input.isEmpty()) return ""

    val describeRegex = """^(.+)-(\d+)-g([0-9a-f]+)$""".toRegex()
    val matchResult = describeRegex.find(input)

    return if (matchResult == null) {
        input.removePrefix("v")
    } else {
        val baseTag = matchResult.groupValues[1]
        baseTag.removePrefix("v")
    }.also {
        logger.lifecycle("version format: $input -> $it")
    }
}
