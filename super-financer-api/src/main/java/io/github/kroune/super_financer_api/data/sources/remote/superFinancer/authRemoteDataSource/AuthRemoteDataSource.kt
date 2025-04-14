package io.github.kroune.super_financer_api.data.sources.remote.superFinancer.authRemoteDataSource

import io.github.kroune.super_financer_api.data.SuperFinancerApiI
import io.github.kroune.super_financer_api.domain.model.UserLoginBody
import io.github.kroune.super_financer_api.domain.model.UserLoginResult
import io.github.kroune.super_financer_api.domain.model.UserRegisterBody
import io.github.kroune.super_financer_api.domain.model.UserRegisterResult
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.IOException

class AuthRemoteDataSource : AuthRemoteDataSourceI, KoinComponent {
    private val apiConstants by inject<SuperFinancerApiI>()
    private val client by inject<HttpClient>()
    private val defaultJson = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    override suspend fun login(data: UserLoginBody): UserLoginResult {
        runCatching {
            val url = apiConstants.url.appendPathSegments("auth", "login").build()
            val request = client.post(url) {
                val encodedValue = Json.encodeToString(data)
                setBody<String>(encodedValue)
            }
            return when (request.status) {
                HttpStatusCode.OK -> {
                    val jwtToken = defaultJson.decodeFromString<String>(request.bodyAsText())
                    UserLoginResult.Success(jwtToken)
                }

                HttpStatusCode.Unauthorized -> {
                    UserLoginResult.InvalidCredentials
                }

                HttpStatusCode.InternalServerError -> {
                    UserLoginResult.ServerError
                }

                HttpStatusCode.TooManyRequests -> {
                    UserLoginResult.TooManyRequests
                }

                else -> {
                    UserLoginResult.UnknownError
                }
            }
        }.onFailure {
            if (it is IOException) {
                return UserLoginResult.NetworkError
            }
        }
        return UserLoginResult.UnknownError
    }

    override suspend fun register(data: UserRegisterBody): UserRegisterResult {
        runCatching {
            val url = apiConstants.url.appendPathSegments("auth", "register").build()
            val request = client.post(url) {
                val encodedValue = Json.encodeToString(data)
                setBody<String>(encodedValue)
            }
            return when (request.status) {
                HttpStatusCode.OK -> {
                    val jwtToken = defaultJson.decodeFromString<String>(request.bodyAsText())
                    UserRegisterResult.Success(jwtToken)
                }

                HttpStatusCode.Conflict -> {
                    UserRegisterResult.LoginConflict
                }

                HttpStatusCode.InternalServerError -> {
                    UserRegisterResult.ServerError
                }

                HttpStatusCode.TooManyRequests -> {
                    UserRegisterResult.TooManyRequests
                }

                else -> {
                    UserRegisterResult.UnknownError
                }
            }
        }.onFailure {
            if (it is IOException) {
                return UserRegisterResult.NetworkError
            }
            it.printStackTrace()
        }
        return UserRegisterResult.UnknownError
    }
}
