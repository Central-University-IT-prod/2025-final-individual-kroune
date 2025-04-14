package io.github.kroune.super_financer_api.data.sources.remote.superFinancer.postsFeedRemoteDataSource

import io.github.kroune.super_financer_api.data.SuperFinancerApiI
import io.github.kroune.super_financer_api.domain.model.NewPostModel
import io.github.kroune.super_financer_api.domain.model.PostsFeedItem
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.appendPathSegments
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PostsFeedRemoteDataSource : PostsFeedRemoteDataSourceI, KoinComponent {
    private val apiConstants by inject<SuperFinancerApiI>()
    private val client by inject<HttpClient>()
    private val defaultJson = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    override suspend fun getFeed(offset: Long, limit: Int, jwtToken: String?): List<PostsFeedItem> {
        val url = apiConstants.url.appendPathSegments("feed").build()
        val request = client.get(url) {
            if (jwtToken != null) {
                bearerAuth(jwtToken)
            }
            parameter("offset", offset)
            parameter("limit", limit)
        }
        return defaultJson.decodeFromString<List<PostsFeedItem>>(request.bodyAsText())
    }

    override suspend fun newPost(data: NewPostModel, jwtToken: String) {
        val url = apiConstants.url.appendPathSegments("feed", "new").build()
        client.post(url) {
            bearerAuth(jwtToken)
            val encodedValue = Json.encodeToString(data)
            setBody<String>(encodedValue)
        }
    }

    override suspend fun likePost(postId: Long, jwtToken: String) {
        val url = apiConstants.url.appendPathSegments("feed", "like").build()
        client.post(url) {
            parameter("postId", postId)
            bearerAuth(jwtToken)
        }
    }

    override suspend fun unlikePost(postId: Long, jwtToken: String) {
        val url = apiConstants.url.appendPathSegments("feed", "like").build()
        client.delete(url) {
            parameter("postId", postId)
            bearerAuth(jwtToken)
        }
    }
}
