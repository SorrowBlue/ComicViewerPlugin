package com.sorrowblue.comicviewer.pdf

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform