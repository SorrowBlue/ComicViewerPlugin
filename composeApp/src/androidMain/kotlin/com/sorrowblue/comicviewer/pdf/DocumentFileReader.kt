package com.sorrowblue.comicviewer.pdf

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.FileProvider
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.android.AndroidDrawDevice
import com.sorrowblue.comicviewer.pdf.aidl.FileReader
import com.sorrowblue.comicviewer.pdf.aidl.IOutputStream
import com.sorrowblue.comicviewer.pdf.aidl.ISeekableInputStream
import java.io.OutputStream
import kotlin.random.Random

internal class DocumentFileReader(
    private val context: android.content.Context,
    private val seekableInputStream: ISeekableInputStream,
    magic: String,
) : FileReader.Stub() {
    init {
        Log.d("DocumentFileReader", "init $this")
    }

    private val document =
        Document.openDocument(MupdfSeekableInputStreamImpl(seekableInputStream), magic)

    private val dir = context.cacheDir.resolve("${Random.nextInt()}/").apply {
        mkdir()
    }

    override fun pageCount() = document.countPages()

    override fun fileSize(pageIndex: Int) = 0L

    override fun loadPage(pageIndex: Int): String? {
//        JavaOutputStream(stream).use {
        val file = dir.resolve("${pageIndex}.webp")
        file.outputStream().use {
            AndroidDrawDevice.drawPageFitWidth(document.loadPage(pageIndex), 600)
                .compress(Bitmap.CompressFormat.WEBP_LOSSY, 50, it)
        }
        val authority = "${context.packageName}.fileprovider"
        val fileUri = FileProvider.getUriForFile(
            context,
            authority,
            file
        )
        val callingPackage = context.packageManager.getNameForUid(getCallingUid())

        if (callingPackage != null) {
            // 読み取り権限を明示的に付与
            context.grantUriPermission(
                callingPackage,
                fileUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } else {
            // エラーハンドリング (通常は発生しない)
            throw SecurityException("Calling package could not be identified.")
        }
        return fileUri.toString()
//        }
    }

    override fun close() {
        Log.d("DocumentFileReader", "close")
        seekableInputStream.close()
    }
}

class JavaOutputStream(private val outputStream: IOutputStream) : OutputStream() {
    override fun write(b: Int) = outputStream.write(b)
    override fun flush() = outputStream.flush()
    override fun close() = outputStream.close()
}