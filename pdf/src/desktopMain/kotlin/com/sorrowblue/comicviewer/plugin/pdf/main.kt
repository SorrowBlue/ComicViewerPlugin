package com.sorrowblue.comicviewer.plugin.pdf

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import comicviewerplugin.pdf.generated.resources.Res
import comicviewerplugin.pdf.generated.resources.ic_product
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComicViewerPdf",
        icon = painterResource(Res.drawable.ic_product)
    ) {
        HomeScreen { }
    }
}
