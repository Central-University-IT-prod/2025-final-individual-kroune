package io.github.kroune.superfinancer.components.webview

import com.arkivanov.decompose.ComponentContext
import io.github.kroune.superfinancer.events.WebViewScreenEvent
import org.koin.core.component.KoinComponent

class WebViewScreenComponent(
    startUrl: String,
    val onNavigationToPosts: (String) -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, KoinComponent, WebViewScreenComponentI {
    override val url = startUrl
    override fun onEvent(event: WebViewScreenEvent) {
        when (event) {
            is WebViewScreenEvent.OnShareButtonClick -> {
                onNavigationToPosts(event.link)
            }
        }
    }
}
