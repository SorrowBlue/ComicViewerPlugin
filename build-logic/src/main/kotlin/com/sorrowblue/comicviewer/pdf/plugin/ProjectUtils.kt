package com.sorrowblue.comicviewer.pdf.plugin

import com.android.build.api.dsl.CommonExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.support.delegates.DependencyHandlerDelegate
import org.gradle.kotlin.dsl.the
import org.gradle.plugin.use.PluginDependency
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension

internal val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()

internal fun Project.plugins(block: PluginManager.() -> Unit) = with(pluginManager, block)

internal fun PluginManager.id(provider: Provider<PluginDependency>) = apply(provider.get().pluginId)

internal inline fun <reified T : CommonExtension<*, *, *, *, *, *>> Project.android(crossinline block: T.() -> Unit) =
    configure<T> { block(this) }

internal inline fun <reified T : KotlinBaseExtension> Project.kotlin(crossinline block: T.() -> Unit) =
    configure<T> { block(this) }

internal fun Project.composeCompiler(block: ComposeCompilerGradlePluginExtension.() -> Unit) =
    configure<ComposeCompilerGradlePluginExtension>(block)

