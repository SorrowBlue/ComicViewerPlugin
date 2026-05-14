package com.sorrowblue.comicviewer.plugin.pdf

import com.sorrowblue.comicviewer.plugin.pdf.home.ExtraNavigator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class DesktopExtraNavigator : ExtraNavigator {
    override val isComicViewerEnabled = false
    override fun launchComicViewer() = Unit
}
