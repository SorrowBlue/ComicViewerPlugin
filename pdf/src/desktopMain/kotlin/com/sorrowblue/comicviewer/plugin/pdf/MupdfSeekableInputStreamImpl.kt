package com.sorrowblue.comicviewer.plugin.pdf

import com.artifex.mupdf.fitz.SeekableInputStream

internal class MupdfSeekableInputStreamImpl(private val seekableInputStream: ISeekableInputStream) :
    SeekableInputStream {

    override fun read(buf: ByteArray) = seekableInputStream.read(buf)

    override fun seek(offset: Long, whence: Int) = seekableInputStream.seek(offset, whence)

    override fun position() = seekableInputStream.position()
}
