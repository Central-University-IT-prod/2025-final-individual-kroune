package io.github.kroune.super_financer_api.domain.model

import kotlinx.serialization.Serializable


sealed interface UserInfoResult {
    data class Success(val data: UserInfoModel) : UserInfoResult
    data object NetworkError : UserInfoResult
    data object UnknownError : UserInfoResult
    data object ServerError : UserInfoResult
    data object TooManyRequests : UserInfoResult
}

@Serializable
data class UserInfoModel(val login: String)
