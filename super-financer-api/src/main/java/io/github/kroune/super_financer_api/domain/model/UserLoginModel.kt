package io.github.kroune.super_financer_api.domain.model

import kotlinx.serialization.Serializable

sealed interface UserLoginResult {
    data class Success(val token: String) : UserLoginResult
    data object NetworkError : UserLoginResult
    data object UnknownError : UserLoginResult
    data object InvalidCredentials : UserLoginResult
    data object ServerError : UserLoginResult
    data object TooManyRequests : UserLoginResult
}

@Serializable
data class UserLoginBody(
    val login: String,
    val password: String
)
