package com.sorrowblue.comicviewer.plugin.pdf

import com.sorrowblue.comicviewer.plugin.pdf.license.LicenseClient
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class LicenseClientImpl : LicenseClient {
    override suspend fun getLibsSource(): String {
        val bytes =
            comicviewerplugin.pdf.desktop.generated.resources.Res.readBytes(
                "files/aboutlibraries.json",
            )
        return bytes.decodeToString()
    }
}
