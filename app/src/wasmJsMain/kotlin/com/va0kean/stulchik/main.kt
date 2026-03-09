package com.va0kean.stulchik

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "compose-target") {
        // Здесь мы будем запускать MainScreen, 
        // когда настроим репозиторий для веба.
        // MainScreen(...)
    }
}
