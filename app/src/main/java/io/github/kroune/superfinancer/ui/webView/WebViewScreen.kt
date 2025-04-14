package io.github.kroune.superfinancer.ui.webView

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import io.github.kroune.superfinancer.R
import io.github.kroune.superfinancer.components.webview.WebViewScreenComponentI
import io.github.kroune.superfinancer.events.WebViewScreenEvent

@Composable
fun WebViewPage(
    component: WebViewScreenComponentI
) {
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            webViewClient = WebViewClient()
            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton({
                component.onEvent(WebViewScreenEvent.OnShareButtonClick(webView.url ?: ""))
            }) {
                Icon(painterResource(R.drawable.share), "share article")
            }
        }
    ) { paddingValues ->
        AndroidView(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            factory = { webView },
            update = {
                it.loadUrl(component.url)
            }
        )
    }
}
