package com.sorrowblue.comicviewer.plugin.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.FileProvider
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.android.AndroidDrawDevice
import com.sorrowblue.comicviewer.plugin.aidl.FileReader
import com.sorrowblue.comicviewer.plugin.aidl.ISeekableInputStream
import kotlin.random.Random

internal class DocumentFileReader(
    private val context: Context,
    private val seekableInputStream: ISeekableInputStream,
    magic: String,
    private val compressFormat: Int,
    private val quality: Int,
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
        return loadPageWithFortmat(pageIndex, compressFormat, quality)
    }

    override fun loadPageWithFortmat(pageIndex: Int, format: Int, quality: Int): String? {
        val compressFormat = when (format) {
            0 -> Bitmap.CompressFormat.JPEG
            1 -> Bitmap.CompressFormat.PNG
            3 -> Bitmap.CompressFormat.WEBP_LOSSY
            4 -> Bitmap.CompressFormat.WEBP_LOSSLESS
            else -> Bitmap.CompressFormat.JPEG
        }
        val file = dir.resolve("${pageIndex}.webp")
        file.outputStream().use {
            AndroidDrawDevice.drawPageFitWidth(document.loadPage(pageIndex), 600)
                .compress(compressFormat, quality, it)
        }
        val authority = "${context.packageName}.fileprovider"
        val fileUri = FileProvider.getUriForFile(
            context,
            authority,
            file
        )
        val callingPackage = context.packageManager.getNameForUid(getCallingUid())

        if (callingPackage != null) {
            context.grantUriPermission(
                callingPackage,
                fileUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } else {
            throw SecurityException("Calling package could not be identified.")
        }
        return fileUri.toString()
    }

    override fun close() {
        Log.d("DocumentFileReader", "close")
        seekableInputStream.close()
    }
}
