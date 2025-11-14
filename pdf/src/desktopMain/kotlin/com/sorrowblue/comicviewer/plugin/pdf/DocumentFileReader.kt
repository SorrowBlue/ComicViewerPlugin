package com.sorrowblue.comicviewer.plugin.pdf

import com.artifex.mupdf.fitz.ColorSpace
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.DrawDevice
import com.artifex.mupdf.fitz.Matrix
import com.artifex.mupdf.fitz.Pixmap
import java.io.OutputStream

internal class DocumentFileReader(
    private val seekableInputStream: ISeekableInputStream,
    magic: String,
) : PdfFileReader {
    private val document: Document

    init {
        println("DocumentFileReader init $this, $seekableInputStream, $magic")
        document = runCatching {
            Document.openDocument(MupdfSeekableInputStreamImpl(seekableInputStream), magic)
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
        println("DocumentFileReader loadDocument $document")
    }

    override fun pageCount() = document.countPages()

    override fun fileSize(pageIndex: Int) = 0L

    override fun loadPage(pageIndex: Int, outputStream: OutputStream) {
        println("DocumentFileReader loadPage(pageIndex: $pageIndex)")
        val page = document.loadPage(pageIndex)
        val bounds = page.bounds
        val pixmap = Pixmap(ColorSpace.DeviceRGB, bounds, false)
        pixmap.clear(0xff)
        val drawDevice = DrawDevice(pixmap)
        val displayList = page.toDisplayList()
        displayList.run(drawDevice, Matrix.Identity(), bounds, null)
        pixmap.asJPEG(80, false).readIntoStream(outputStream)
    }

    override fun close() {
        println("DocumentFileReader close")
        seekableInputStream.close()
    }
}
