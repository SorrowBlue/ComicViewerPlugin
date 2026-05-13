plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false

    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false

    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false

    alias(libs.plugins.aboutlibraries) apply false
    alias(libs.plugins.buildconfig) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.metro) apply false

    alias(libs.plugins.versionCatalogLinter)
}

val reportMerge = tasks.register("reportMerge", dev.detekt.gradle.report.ReportMergeTask::class) {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif"))
}

subprojects {
    reportMerge {
        input.from(
            tasks.withType<dev.detekt.gradle.Detekt>()
                .map { it.reports.sarif.outputLocation })
    }
}

tasks.withType<UpdateDaemonJvm> {
    vendor = JvmVendorSpec.ADOPTIUM
    languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
}
