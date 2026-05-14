package com.sorrowblue.comicviewer.plugin.pdf

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
internal interface AppGraph {

    val pdfPluginAppClass: PdfPluginAppClass

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(): AppGraph
    }
}
