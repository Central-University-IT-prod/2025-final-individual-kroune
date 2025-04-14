package io.github.kroune.superfinancer.components.webview

import io.github.kroune.superfinancer.events.WebViewScreenEvent

interface WebViewScreenComponentI {
    val url: String
    fun onEvent(event: WebViewScreenEvent)
}
