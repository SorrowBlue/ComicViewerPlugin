package com.sorrowblue.comicviewer.plugin.pdf.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.visible
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.plugin.pdf.icon.MaterialSymbolsLicense
import comicviewerplugin.pdf.generated.resources.Res
import comicviewerplugin.pdf.generated.resources.home_btn_app_launch
import comicviewerplugin.pdf.generated.resources.home_btn_license
import comicviewerplugin.pdf.generated.resources.home_msg_plugin_desc
import comicviewerplugin.pdf.generated.resources.home_title
import comicviewerplugin.pdf.generated.resources.ic_product
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Serializable
internal data object Home : NavKey

@Composable
internal fun HomeScreen(
    extraNavigator: ExtraNavigator,
    onLicenseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.home_title))
                },
            )
        },
        modifier = modifier,
    ) { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_product),
                contentDescription = "アプリアイコン",
                modifier = Modifier.height(108.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.home_msg_plugin_desc),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                modifier = Modifier.visible(extraNavigator.isComicViewerEnabled),
                onClick = {
                    extraNavigator.launchComicViewer()
                },
            ) {
                Text(stringResource(Res.string.home_btn_app_launch))
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onLicenseClick,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Icon(
                    imageVector = MaterialSymbolsLicense,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(Res.string.home_btn_license))
            }
        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            extraNavigator = object : ExtraNavigator {
                override fun launchComicViewer() = Unit
                override val isComicViewerEnabled = true
            },
            onLicenseClick = {},
        )
    }
}
