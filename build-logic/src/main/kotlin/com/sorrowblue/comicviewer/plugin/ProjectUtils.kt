package com.sorrowblue.comicviewer.plugin

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension

internal val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()

internal fun Project.plugins(block: PluginManager.() -> Unit) = with(pluginManager, block)

internal inline fun <reified T : KotlinBaseExtension> Project.kotlin(crossinline block: T.() -> Unit) =
    configure<T> { block(this) }
