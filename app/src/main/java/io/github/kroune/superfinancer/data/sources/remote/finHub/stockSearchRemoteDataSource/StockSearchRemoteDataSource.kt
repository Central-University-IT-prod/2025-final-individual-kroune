package io.github.kroune.superfinancer.data.sources.remote.finHub.stockSearchRemoteDataSource

import io.github.kroune.superfinancer.data.FinHubApiI
import io.github.kroune.superfinancer.domain.models.StockSearch
import io.github.kroune.superfinancer.domain.models.StockSearchApiResponse
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

class StockSearchRemoteDataSource : StockSearchRemoteDataSourceI, KoinComponent {
    private val apiConstants by inject<FinHubApiI>()
    private val client by inject<HttpClient>()
    private val defaultJson = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    override suspend fun search(query: String): StockSearchApiResponse<StockSearch> {
        runCatching {
            val url = apiConstants.url.appendPathSegments("search").build()
            val request = client.get(url) {
                parameter("q", query)
                parameter("token", apiConstants.token)
                parameter("exchange", "US")
            }
            return when (request.status) {
                HttpStatusCode.OK -> {
                    StockSearchApiResponse.Success(
                        defaultJson.decodeFromString(request.bodyAsText())
                    )
                }

                HttpStatusCode.TooManyRequests -> {
                    StockSearchApiResponse.TooManyRequests()
                }

                HttpStatusCode.InternalServerError -> {
                    StockSearchApiResponse.ServerError()
                }

                else -> {
                    StockSearchApiResponse.UnknownError()
                }
            }
        }.onFailure {
            if (it is IOException) {
                return StockSearchApiResponse.NetworkError()
            }
            it.printStackTrace()
        }
        return StockSearchApiResponse.UnknownError()
    }
}
