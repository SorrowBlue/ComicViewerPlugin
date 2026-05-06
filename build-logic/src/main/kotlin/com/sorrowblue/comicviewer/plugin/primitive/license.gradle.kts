package com.sorrowblue.comicviewer.plugin.primitive

import com.mikepenz.aboutlibraries.plugin.StrictMode

plugins {
    com.mikepenz.aboutlibraries.plugin
}

aboutLibraries {
    collect {
        includePlatform.set(true)
        fetchRemoteLicense.set(true)
        val githubApiToken: String? by project
        gitHubApiToken.set(githubApiToken)
    }
    license {
        strictMode.set(StrictMode.FAIL)
        allowedLicenses.addAll("Apache-2.0")
        allowedLicensesMap.putAll(
            mapOf(
                "AGPL-3.0" to listOf("com.sorrowblue.mupdf"),
                "GNU Affero General Public License version 3.0" to listOf("com.sorrowblue.mupdf"),
            ),
        )
    }
}
