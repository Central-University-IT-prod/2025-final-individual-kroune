package io.github.kroune.superfinancer.events

sealed interface WebViewScreenEvent {
    data class OnShareButtonClick(val link: String) : WebViewScreenEvent
}
