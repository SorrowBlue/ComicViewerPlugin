package com.sorrowblue.comicviewer.plugin.pdf

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class)
internal interface AppGraph {

    val pdfPluginAppClass: PdfPluginAppClass

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides context: Context): AppGraph
    }
}
