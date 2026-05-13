package com.sorrowblue.comicviewer.plugin.pdf.license

import com.sorrowblue.comicviewer.plugin.pdf.NavKeyEntryProvider
import com.sorrowblue.comicviewer.plugin.pdf.NavKeyModuleBuilder
import com.sorrowblue.comicviewer.plugin.pdf.NavigationScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavigationScope::class)
interface LicenseModule {

    @IntoSet
    @Provides
    fun provideLicenseSubClass(): NavKeyModuleBuilder = {
        subclass(License::class, License.serializer())
    }

    @IntoSet
    @Provides
    fun provideLicenseEntry(client: LicenseClient): NavKeyEntryProvider = { navigator ->
        entry<License> {
            LicenseScreen(
                libsSource = { client.getLibsSource() },
                onBackClick = navigator::onBack
            )
        }
    }
}
