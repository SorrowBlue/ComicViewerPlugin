package com.sorrowblue.comicviewer.plugin.pdf

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        composable("home") {
                            var visibleIcon by remember { mutableStateOf(getLauncherIconVisible()) }
                            HomeScreen(
                                isAndroid = true,
                                visibleIcon = visibleIcon,
                                onLicenseClick = {
                                    navController.navigate("license")
                                },
                                onLaunchAppClick = {
                                    launchApp()
                                },
                                onVisibleChange = { visible ->
                                    setLauncherIconVisible(visible)
                                    visibleIcon = getLauncherIconVisible()
                                },
                            )
                        }
                        composable("license") {
                            LicenseScreen()
                        }
                    }
                }
            }
        }
    }

    private fun launchApp() {
        val isLaunched = runCatching {
            var launched = false
            for (pkg in TargetPackages) {
                val intent = packageManager.getLaunchIntentForPackage(pkg)
                if (intent != null) {
                    startActivity(intent)
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
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "Error: ${e.localizedMessage.orEmpty()}")
            val intent = Intent(Intent.ACTION_VIEW, browserUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    private fun setLauncherIconVisible(visible: Boolean) {
        val componentName =
            ComponentName(this, "com.sorrowblue.comicviewer.plugin.pdf.LauncherAlias")
        val newState = if (visible) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }

        packageManager.setComponentEnabledSetting(
            componentName,
            newState,
            PackageManager.DONT_KILL_APP,
        )
    }

    private fun getLauncherIconVisible(): Boolean {
        val componentName =
            ComponentName(this, "com.sorrowblue.comicviewer.plugin.pdf.LauncherAlias")
        return packageManager.getComponentEnabledSetting(
            componentName,
        ) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
    }
}

private const val TAG = "MainActivity"

private const val BasePackageName = "com.sorrowblue.comicviewer"
private val TargetPackages = listOf(
    "$BasePackageName.debug",
    "$BasePackageName.prerelease",
    BasePackageName,
)
