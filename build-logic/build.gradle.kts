import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.kotlin.dsl.withType

plugins {
    `kotlin-dsl`
    alias(libs.plugins.detekt)
}

group = "com.sorrowblue.comicviewer.pdf.buildlogic"

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
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kotlin.compose.gradlePlugin)
//    compileOnly(libs.kotlinx.kover.gradlePlugin)
//    compileOnly(libs.google.ksp.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
//    compileOnly(libs.dokka.gradlePlugin)
//    compileOnly(libs.compose.gradlePlugin)
//    compileOnly(libs.licensee.gradlePlugin)
//    compileOnly(libs.aboutlibraries.gradlePlugin)
    compileOnly(files(currentLibs.javaClass.superclass.protectionDomain.codeSource.location))
//    detektPlugins(libs.nlopez.compose.rules.detekt)
//    detektPlugins(libs.arturbosch.detektFormatting)
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
        register(libs.plugins.emptyPlugin) {
            implementationClass = "com.sorrowblue.comicviewer.pdf.plugin.EmptyPlugin"
        }
        register(libs.plugins.detektPlugin) {
            implementationClass = "com.sorrowblue.comicviewer.pdf.plugin.DetektConventionPlugin"
        }
    }
}

// Temporarily set to PushMode only
private val currentLibs get() = libs

private fun NamedDomainObjectContainer<PluginDeclaration>.register(
    provider: Provider<PluginDependency>,
    function: PluginDeclaration.() -> Unit,
) = register(provider.get().pluginId) {
    id = name
    function()
}
