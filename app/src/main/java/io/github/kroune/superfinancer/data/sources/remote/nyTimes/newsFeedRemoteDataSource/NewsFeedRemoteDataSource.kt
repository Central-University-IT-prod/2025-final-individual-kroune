package io.github.kroune.superfinancer.data.sources.remote.nyTimes.newsFeedRemoteDataSource

import io.github.kroune.superfinancer.data.NYTimesApiI
import io.github.kroune.superfinancer.domain.models.NewsFeedModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.appendPathSegments
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NewsFeedRemoteDataSource : KoinComponent, NewFeedRemoteDataSourceI {
    private val apiConstants by inject<NYTimesApiI>()
    private val client by inject<HttpClient>()
    private val defaultJson = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    override suspend fun getNewsFeed(offset: Int, amount: Int): NewsFeedModel {
        val url = apiConstants.url.appendPathSegments(
            "news", "v3", "content", "all", "all.json"
        ).build()
        val request = client.get(url) {
            url {
                parameter("offset", offset)
                parameter("limit", amount)
                parameter("api-key", apiConstants.token)
            }
        }
        return defaultJson.decodeFromString<NewsFeedModel>(request.bodyAsText())
    }
}
