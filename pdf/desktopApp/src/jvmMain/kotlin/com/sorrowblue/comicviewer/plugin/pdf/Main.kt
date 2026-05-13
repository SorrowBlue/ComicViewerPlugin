package com.sorrowblue.comicviewer.plugin.pdf

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import comicviewerplugin.pdf.generated.resources.Res as PdfRes
import comicviewerplugin.pdf.generated.resources.ic_product
import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    val graph by lazy {
        createGraphFactory<AppGraph.Factory>().create()
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComicViewerPdf",
        icon = painterResource(PdfRes.drawable.ic_product),
    ) {
        graph.pdfPluginAppClass()
    }
    CoroutineScope(Dispatchers.Main).launch {
        delay(1000)
        println("delay(1000)")
    }
}
