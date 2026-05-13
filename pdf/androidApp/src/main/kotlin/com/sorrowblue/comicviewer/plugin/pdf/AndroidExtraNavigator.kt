package com.sorrowblue.comicviewer.plugin.pdf

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import com.sorrowblue.comicviewer.plugin.pdf.home.ExtraNavigator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

private const val TAG = "AndroidExtraNavigator"

private const val BasePackageName = "com.sorrowblue.comicviewer"
private val TargetPackages = listOf(
    "$BasePackageName.debug",
    "$BasePackageName.prerelease",
    BasePackageName,
)

@ContributesBinding(AppScope::class)
internal class AndroidExtraNavigator(private val context: Context) : ExtraNavigator {
    override val isComicViewerEnabled = true
    override fun launchComicViewer() {
        launchApp()
    }

    private fun launchApp() {
        val isLaunched = runCatching {
            var launched = false
            for (pkg in TargetPackages) {
                val intent = context.packageManager.getLaunchIntentForPackage(pkg)?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                if (intent != null) {
                    context.startActivity(intent)
                    launched = true
                    break
                }
            }
            launched
        }.onFailure {
            Log.e(TAG, "Error: ${it.localizedMessage.orEmpty()}")
        }.getOrDefault(false)
        if (!isLaunched) {
            openPlayStore(BasePackageName)
        }
    }

    private fun openPlayStore(packageName: String) {
        val marketUri = "market://details?id=$packageName".toUri()
        val browserUri = "https://play.google.com/store/apps/details?id=$packageName".toUri()

        try {
            val intent = Intent(Intent.ACTION_VIEW, marketUri).apply {
                setPackage("com.android.vending")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "Error: ${e.localizedMessage.orEmpty()}")
            val intent = Intent(Intent.ACTION_VIEW, browserUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}
