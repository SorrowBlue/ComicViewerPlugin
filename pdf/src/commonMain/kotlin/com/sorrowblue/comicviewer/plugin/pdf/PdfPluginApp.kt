package com.sorrowblue.comicviewer.plugin.pdf

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Inject
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

typealias NavKeyEntryProvider = EntryProviderScope<NavKey>.(Navigator) -> Unit
typealias NavKeyModuleBuilder = PolymorphicModuleBuilder<NavKey>.() -> Unit

annotation class NavigationScope
annotation class PdfPluginAppScope

@GraphExtension(scope = PdfPluginAppScope::class, additionalScopes = [NavigationScope::class])
interface PdfPluginAppContext {
    val rootScreen: NavKey
    val entryList: Set<NavKeyEntryProvider>
    val polymorphicDefinitions: Set<NavKeyModuleBuilder>

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun createPdfPluginAppContext(): PdfPluginAppContext
    }
}

interface Navigator {
    val backStack: NavBackStack<NavKey>
    fun onBack()
    fun add(navKey: NavKey)
}

@Composable
fun rememberNavigator(config: SavedStateConfiguration, root: NavKey): NavigatorImpl {
    val backStack = rememberNavBackStack(config, root)
    return remember { NavigatorImpl(backStack) }
}

class NavigatorImpl(override val backStack: NavBackStack<NavKey>) : Navigator {
    override fun onBack() {
        backStack.removeLastOrNull()
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    override fun add(navKey: NavKey) {
        backStack.add(navKey)
    }
}

@Inject
class PdfPluginAppClass(private val factory: PdfPluginAppContext.Factory) {
    @Composable
    operator fun invoke() {
        val pluginAppScope = retain { factory.createPdfPluginAppContext() }
        PdfPluginApp(pluginAppScope)
    }
}

@Composable
private fun PdfPluginApp(pluginAppScope: PdfPluginAppContext) {
    val config = retain {
        SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    pluginAppScope.polymorphicDefinitions.forEach {
                        it.invoke(this)
                    }
                }
            }
        }
    }
    val navigator = rememberNavigator(config, pluginAppScope.rootScreen)
    MaterialTheme {
        NavDisplay(
            backStack = navigator.backStack,
            entryProvider = entryProvider {
                pluginAppScope.entryList.forEach {
                    it.invoke(this, navigator)
                }
            },
        )
    }
}
