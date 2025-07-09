package com.sorrowblue.comicviewer.pdf

import com.sorrowblue.comicviewer.pdf.aidl.ISeekableInputStream

internal class MupdfSeekableInputStreamImpl(private val seekableInputStream: ISeekableInputStream) :
    com.artifex.mupdf.fitz.SeekableInputStream {

    override fun read(buf: ByteArray) = seekableInputStream.read(buf)

    override fun seek(offset: Long, whence: Int) = seekableInputStream.seek(offset, whence)

    override fun position() = seekableInputStream.position()
}
