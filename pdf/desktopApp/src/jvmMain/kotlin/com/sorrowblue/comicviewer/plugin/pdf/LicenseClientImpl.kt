package com.sorrowblue.comicviewer.plugin.pdf

import com.sorrowblue.comicviewer.plugin.pdf.home.ExtraNavigator
import com.sorrowblue.comicviewer.plugin.pdf.license.LicenseClient
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.DependencyGraph

@ContributesBinding(AppScope::class)
internal class LicenseClientImpl : LicenseClient {
    override suspend fun getLibsSource(): String {
        val bytes =
            comicviewerplugin.pdf.desktop.generated.resources.Res.readBytes(
                "files/aboutlibraries.json",
            )
        return bytes.decodeToString()
    }
}

@DependencyGraph(AppScope::class)
internal interface AppGraph {

    val pdfPluginAppClass: PdfPluginAppClass

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(): AppGraph
    }
}

@ContributesBinding(AppScope::class)
internal class AndroidExtraNavigator : ExtraNavigator {
    override val isComicViewerEnabled = false
    override fun launchComicViewer() = Unit
}
