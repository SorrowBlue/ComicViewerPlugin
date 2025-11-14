package com.sorrowblue.comicviewer.plugin.pdf

import com.sorrowblue.mupdf.kmp.MuPDF
import om.sorrowblue.comicviewer.pdf.BuildKonfig

interface PdfPlugin {
    val version: String
    val timestamp: Long

    fun init()
    fun getReader(seekableInputStream: ISeekableInputStream, magic: String): PdfFileReader
}

internal class PdfPluginImpl : PdfPlugin {

    override val version = BuildKonfig.VERSION_NAME
    override val timestamp = BuildKonfig.TIMESTAMP

    override fun init() {
        MuPDF.init()
    }

    override fun getReader(
        seekableInputStream: ISeekableInputStream,
        magic: String,
    ): PdfFileReader {
        return DocumentFileReader(seekableInputStream, magic)
    }
}
