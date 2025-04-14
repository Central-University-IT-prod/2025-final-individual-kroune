package io.github.kroune.superfinancer.data.json

import android.content.Context
import io.github.kroune.superfinancer.R
import kotlinx.serialization.json.Json

class DefaultJsonProvider(context: Context) : DefaultJsonProviderI {
    init {
        with(context) {
            val text = resources.openRawResource(R.raw.tickers_list_data).bufferedReader().readText()
            tickersList = Json.decodeFromString(text)
        }
    }
    override var tickersList: List<String>
}
