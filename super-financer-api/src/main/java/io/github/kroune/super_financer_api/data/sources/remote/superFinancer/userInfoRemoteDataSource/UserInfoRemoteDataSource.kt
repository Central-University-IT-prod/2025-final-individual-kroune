package io.github.kroune.super_financer_api.data.sources.remote.superFinancer.userInfoRemoteDataSource

import io.github.kroune.super_financer_api.data.SuperFinancerApiI
import io.github.kroune.super_financer_api.domain.model.UserInfoModel
import io.github.kroune.super_financer_api.domain.model.UserInfoResult
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

class UserInfoRemoteDataSource : UserInfoRemoteDataSourceI, KoinComponent {
    private val apiConstants by inject<SuperFinancerApiI>()
    private val client by inject<HttpClient>()
    private val defaultJson = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    override suspend fun getUserInfo(userId: Long): UserInfoResult {
        runCatching {
            val url = apiConstants.url.appendPathSegments("user").build()
            val request = client.get(url) {
                parameter("userId", userId)
            }
            return when (request.status) {
                HttpStatusCode.OK -> {
                    val userInfo = defaultJson.decodeFromString<UserInfoModel>(request.bodyAsText())
                    UserInfoResult.Success(userInfo)
                }

                HttpStatusCode.InternalServerError -> {
                    UserInfoResult.ServerError
                }

                HttpStatusCode.TooManyRequests -> {
                    UserInfoResult.TooManyRequests
                }

                else -> {
                    UserInfoResult.UnknownError
                }
            }
        }.onFailure {
            if (it is IOException) {
                return UserInfoResult.NetworkError
            }
            it.printStackTrace()
        }
        return UserInfoResult.UnknownError
    }
}
