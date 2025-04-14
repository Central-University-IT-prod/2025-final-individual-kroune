package io.github.kroune.super_financer_api.domain.model

import kotlinx.serialization.Serializable

sealed interface UserRegisterResult {
    data class Success(val token: String) : UserRegisterResult
    data object NetworkError : UserRegisterResult
    data object UnknownError : UserRegisterResult
    data object LoginConflict : UserRegisterResult
    data object ServerError : UserRegisterResult
    data object TooManyRequests : UserRegisterResult
}

@Serializable
data class UserRegisterBody(
    val login: String,
    val password: String
)
