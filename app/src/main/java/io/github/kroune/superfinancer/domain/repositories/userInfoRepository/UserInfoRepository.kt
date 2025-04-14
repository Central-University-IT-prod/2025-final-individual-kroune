package io.github.kroune.superfinancer.domain.repositories.userInfoRepository

import io.github.kroune.super_financer_api.data.sources.remote.superFinancer.userInfoRemoteDataSource.UserInfoRemoteDataSourceI
import io.github.kroune.super_financer_api.domain.model.UserInfoResult
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserInfoRepository : UserInfoRepositoryI, KoinComponent {
    private val remote by inject<UserInfoRemoteDataSourceI>()
    override suspend fun getUserInfo(userId: Long): UserInfoResult {
        return remote.getUserInfo(userId)
    }
}
