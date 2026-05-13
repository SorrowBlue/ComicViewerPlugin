package com.sorrowblue.comicviewer.plugin.pdf.license

interface LicenseClient {

    suspend fun getLibsSource(): String
}
