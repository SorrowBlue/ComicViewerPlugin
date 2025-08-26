import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.kotlin.dsl.withType

plugins {
    `kotlin-dsl`
    alias(libs.plugins.detekt)
}

group = "com.sorrowblue.comicviewer.plugin.buildlogic"

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

dependencies {
    compileOnly(files(currentLibs.javaClass.superclass.protectionDomain.codeSource.location))
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    detektPlugins(libs.detekt.compose)
    detektPlugins(libs.detekt.formatting)
}

detekt {
    buildUponDefaultConfig = true
    autoCorrect = true
    config.setFrom(layout.projectDirectory.file("../config/detekt/detekt.yml"))
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(false)
        md.required.set(false)
        sarif.required.set(true)
        txt.required.set(false)
        xml.required.set(false)
    }
}

tasks.register("detektAll") {
    group = "verification"
    dependsOn(tasks.withType<Detekt>())
}

gradlePlugin {
    plugins {
        register(libs.plugins.comicviewer.detekt) {
            implementationClass = "com.sorrowblue.comicviewer.plugin.DetektConventionPlugin"
        }
        register(libs.plugins.comicviewer.gitTagVersion) {
            implementationClass = "com.sorrowblue.comicviewer.plugin.GitTagVersionPlugin"
        }
        register(libs.plugins.comicviewer.lint) {
            implementationClass = "com.sorrowblue.comicviewer.plugin.AndroidLintConventionPlugin"
        }
    }
}

private val currentLibs get() = libs

private fun NamedDomainObjectContainer<PluginDeclaration>.register(
    provider: Provider<PluginDependency>,
    function: PluginDeclaration.() -> Unit,
) = register(provider.get().pluginId) {
    id = name
    function()
}
