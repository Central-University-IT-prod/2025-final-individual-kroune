package io.github.kroune.super_financer_api.data.sources.remote.superFinancer.userInfoRemoteDataSource

import io.github.kroune.super_financer_api.domain.model.UserInfoResult

interface UserInfoRemoteDataSourceI {
    suspend fun getUserInfo(userId: Long): UserInfoResult
}
