package com.sorrowblue.comicviewer.plugin.pdf

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.zacsweers.metro.createGraphFactory

internal class MainApplication : Application() {
    val graph by lazy {
        createGraphFactory<AppGraph.Factory>().create(this)
    }
}

internal class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val graph = (application as MainApplication).graph
        setContent {
            graph.pdfPluginAppClass()
        }
    }
}
