package io.github.kroune.superfinancer.data.sources.remote.nyTimes.articlesSearchRemoteDataSource

import io.github.kroune.superfinancer.data.NYTimesApiI
import io.github.kroune.superfinancer.domain.models.ArticlesSearchApiResponse
import io.github.kroune.superfinancer.domain.models.ArticlesSearchModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.TooManyRequests
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.http.appendPathSegments
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.IOException

class ArticlesSearchRemoteDataSource : ArticlesSearchRemoteDataSourceI, KoinComponent {
    private val apiConstants by inject<NYTimesApiI>()
    private val client by inject<HttpClient>()
    private val defaultJson = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    override suspend fun searchForArticle(query: String): ArticlesSearchApiResponse {
        runCatching {
            val url = apiConstants.url.appendPathSegments(
                "search", "v2", "articlesearch.json"
            ).build()
            val request = client.get(url) {
                parameter("q", query)
                parameter("api-key", apiConstants.token)
            }
            return when (request.status) {
                Unauthorized -> {
                    ArticlesSearchApiResponse.Unauthorized
                }

                TooManyRequests -> {
                    ArticlesSearchApiResponse.TooManyRequests
                }

                OK -> {
                    ArticlesSearchApiResponse.Success(
                        defaultJson.decodeFromString<ArticlesSearchModel>(request.bodyAsText())
                    )
                }

                else -> {
                    ArticlesSearchApiResponse.UnknownError
                }
            }
        }.onFailure {
            if (it is IOException) {
                return ArticlesSearchApiResponse.NetworkError
            }
        }
        return ArticlesSearchApiResponse.UnknownError
    }
}
