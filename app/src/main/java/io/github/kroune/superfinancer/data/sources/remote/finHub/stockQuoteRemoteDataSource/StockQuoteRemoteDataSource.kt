package io.github.kroune.superfinancer.data.sources.remote.finHub.stockQuoteRemoteDataSource

import io.github.kroune.superfinancer.data.FinHubApiI
import io.github.kroune.superfinancer.domain.models.StockQuote
import io.github.kroune.superfinancer.domain.models.StockQuoteApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.IOException

class StockQuoteRemoteDataSource : StockQuoteRemoteDataSourceI, KoinComponent {
    private val apiConstants by inject<FinHubApiI>()
    private val client by inject<HttpClient>()
    private val defaultJson = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    override suspend fun getQuote(stock: String): StockQuoteApiResponse<StockQuote> {
        return runCatching {
            val url = apiConstants.url.appendPathSegments("quote").build()
            val request = client.get(url) {
                parameter("symbol", stock)
                parameter("token", apiConstants.token)
            }
            return when (request.status) {
                HttpStatusCode.OK -> {
                    StockQuoteApiResponse.Success(
                        defaultJson.decodeFromString<StockQuote>(request.bodyAsText())
                    )
                }

                HttpStatusCode.TooManyRequests -> {
                    StockQuoteApiResponse.TooManyRequests()
                }

                HttpStatusCode.InternalServerError -> {
                    StockQuoteApiResponse.ServerError()
                }

                else -> {
                    StockQuoteApiResponse.UnknownError()
                }
            }
        }.getOrElse {
            if (it is IOException) {
                return@getOrElse StockQuoteApiResponse.NetworkError()
            }
            it.printStackTrace()
            StockQuoteApiResponse.UnknownError()
        }
    }
}
