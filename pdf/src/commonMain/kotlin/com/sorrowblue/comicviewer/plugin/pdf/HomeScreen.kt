package com.sorrowblue.comicviewer.plugin.pdf

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.visible
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.plugin.pdf.icon.License
import comicviewerplugin.pdf.generated.resources.Res
import comicviewerplugin.pdf.generated.resources.ic_product
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    isAndroid: Boolean = false,
    visibleIcon: Boolean = false,
    onLicenseClick: () -> Unit = {},
    onLaunchAppClick: () -> Unit = {},
    onVisibleChange: (Boolean) -> Unit = {},
) {
    Scaffold(modifier = modifier) { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // アプリアイコン
            Image(
                painter = painterResource(Res.drawable.ic_product),
                contentDescription = "アプリアイコン",
                modifier = Modifier.height(108.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            // 機能説明
            Text(
                text = "このアプリはComicViewerのプラグインです。ComicViewerでPDFファイルを読み込むことができます。",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(24.dp))

            // ホームランチャーアイコン表示切り替えスイッチ
            Row(
                modifier = Modifier
                    .visible(isAndroid)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "ホームランチャーにアイコンを表示",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Switch(
                    checked = visibleIcon,
                    onCheckedChange = {
                        onVisibleChange(it)
                    },
                )
            }
            Spacer(modifier = Modifier.height(16.dp)) // Adjusted spacer

            // ComicViewerアプリを起動ボタン
            OutlinedButton(
                modifier = Modifier.visible(isAndroid),
                onClick = {
                    onLaunchAppClick()
                }) {
                Text("ComicViewerアプリを起動")
            }

            Spacer(modifier = Modifier.height(8.dp)) // Adjusted spacer

            // ライセンスボタン
            OutlinedButton(
                onClick = onLicenseClick,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Icon(
                    imageVector = License,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("ライセンス")
            }
        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}
