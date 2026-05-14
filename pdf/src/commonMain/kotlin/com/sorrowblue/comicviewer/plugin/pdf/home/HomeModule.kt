package com.sorrowblue.comicviewer.plugin.pdf.home

import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.plugin.pdf.NavKeyEntryProvider
import com.sorrowblue.comicviewer.plugin.pdf.NavKeyModuleBuilder
import com.sorrowblue.comicviewer.plugin.pdf.NavigationScope
import com.sorrowblue.comicviewer.plugin.pdf.license.License
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavigationScope::class)
interface HomeModule {

    @Provides
    fun rootScreen(): NavKey = Home

    @IntoSet
    @Provides
    fun provideHomeSubClass(): NavKeyModuleBuilder = {
        subclass(Home::class, Home.serializer())
    }

    @IntoSet
    @Provides
    fun provideHomeEntry(extraNavigator: ExtraNavigator): NavKeyEntryProvider = { navigator ->
        entry<Home> {
            HomeScreen(
                extraNavigator = extraNavigator,
                onLicenseClick = {
                    navigator.add(License)
                },
            )
        }
    }
}
