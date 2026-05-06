package com.sorrowblue.comicviewer.plugin.pdf

import com.sorrowblue.mupdf.kmp.MuPDF

interface PdfPlugin {
    val version: String
    val timestamp: Long

    fun init()
    fun getReader(seekableInputStream: ISeekableInputStream, magic: String): PdfFileReader
}

internal class PdfPluginImpl : PdfPlugin {

    override val version = BuildConfig.VERSION_NAME
    override val timestamp = BuildConfig.TIMESTAMP

    override fun init() {
        if (!MuPDF.isInitializedSuccessfully) {
            MuPDF.init()
        }
    }

    override fun getReader(
        seekableInputStream: ISeekableInputStream,
        magic: String,
    ): PdfFileReader = DocumentFileReader(seekableInputStream, magic)
}
