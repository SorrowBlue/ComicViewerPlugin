package com.sorrowblue.comicviewer.plugin.pdf

import com.artifex.mupdf.fitz.ColorSpace
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.DrawDevice
import com.artifex.mupdf.fitz.Matrix
import com.artifex.mupdf.fitz.Pixmap
import com.artifex.mupdf.fitz.SeekableInputStream
import java.io.OutputStream

interface ISeekableInputStream {

    fun read(buf: ByteArray): Int

    fun seek(offset: Long, whence: Int): Long

    fun position(): Long

    fun close()
}

internal interface FileReader {
    fun pageCount(): Int
    fun loadPage(pageIndex: Int, outputStream: OutputStream)
    fun fileSize(pageIndex: Int): Long

    fun close()
}


internal class MupdfSeekableInputStreamImpl(private val seekableInputStream: ISeekableInputStream) :
    SeekableInputStream {

    override fun read(buf: ByteArray) = seekableInputStream.read(buf)

    override fun seek(offset: Long, whence: Int) = seekableInputStream.seek(offset, whence)

    override fun position() = seekableInputStream.position()
}

class DocumentFileReader(
    private val seekableInputStream: ISeekableInputStream,
    magic: String,
) : FileReader {
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
