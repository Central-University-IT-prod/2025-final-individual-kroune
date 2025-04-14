package io.github.kroune.superfinancer.domain.repositories.userInfoRepository

import io.github.kroune.super_financer_api.domain.model.UserInfoResult

interface UserInfoRepositoryI {
    suspend fun getUserInfo(userId: Long): UserInfoResult
}
