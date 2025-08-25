package com.sorrowblue.comicviewer.plugin.pdf

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.sorrowblue.comicviewer.plugin.aidl.CompressFormat
import com.sorrowblue.comicviewer.plugin.aidl.FileReader
import com.sorrowblue.comicviewer.plugin.pdf.aidl.IRemotePdfService
import com.sorrowblue.comicviewer.plugin.aidl.ISeekableInputStream
import com.sorrowblue.mupdf.kmp.MuPDF

class PdfService : Service() {

    override fun onCreate() {
        super.onCreate()
        MuPDF.init()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private val binder = object : IRemotePdfService.Stub() {
        override fun getFIleReader(inputStream: ISeekableInputStream, magic: String): FileReader {
            return DocumentFileReader(this@PdfService.applicationContext, inputStream, magic, compressFormat, quality)
        }

        private var compressFormat: Int = CompressFormat.JPEG
        override fun getCompressFormat(): Int {
            return compressFormat
        }
        override fun setCompressFormat(value: Int) {
            this.compressFormat = value
        }

        private var quality: Int = 100
        override fun getQuality(): Int {
            return quality
        }
        override fun setQuality(quality: Int) {
            this.quality = quality
        }

        override fun getVersion(): String? {
            return BuildConfig.VERSION_NAME
        }
        override fun getVersion2(): String? {
            return BuildConfig.VERSION_NAME
        }
    }
}
