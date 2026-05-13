package com.sorrowblue.comicviewer.plugin.pdf

import android.content.Context
import com.sorrowblue.comicviewer.plugin.pdf.app.R
import com.sorrowblue.comicviewer.plugin.pdf.license.LicenseClient
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class LicenseClientImpl(private val context: Context) : LicenseClient {
    override suspend fun getLibsSource(): String =
        context.resources.openRawResource(R.raw.aboutlibraries).readBytes().decodeToString()
}
