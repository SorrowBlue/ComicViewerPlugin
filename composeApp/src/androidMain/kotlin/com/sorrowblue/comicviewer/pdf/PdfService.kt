package com.sorrowblue.comicviewer.pdf

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.sorrowblue.comicviewer.pdf.aidl.FileReader
import com.sorrowblue.comicviewer.pdf.aidl.IRemotePdfService
import com.sorrowblue.comicviewer.pdf.aidl.ISeekableInputStream
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
            return DocumentFileReader(this@PdfService.applicationContext, inputStream, magic)
        }
    }
}