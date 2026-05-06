package com.sorrowblue.comicviewer.plugin.primitive

import com.sorrowblue.comicviewer.plugin.libs
import dev.detekt.gradle.Detekt

plugins {
    dev.detekt
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
    basePath = rootProject.projectDir
    autoCorrect = true
    parallel = true
}

dependencies {
    detektPlugins(libs.bundles.detekt)
}

tasks.withType<Detekt>().configureEach {
    reports {
        sarif.required.set(true)
        html.required.set(false)
        markdown.required.set(false)
        checkstyle.required.set(false)
    }
    exclude {
        it.file.path.run {
            contains("generated") || contains("buildkonfig")
        }
    }
}
