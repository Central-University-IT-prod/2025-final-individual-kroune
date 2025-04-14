package io.github.kroune.super_financer_api.data.sources.remote.superFinancer.authRemoteDataSource

import io.github.kroune.super_financer_api.domain.model.UserLoginBody
import io.github.kroune.super_financer_api.domain.model.UserLoginResult
import io.github.kroune.super_financer_api.domain.model.UserRegisterBody
import io.github.kroune.super_financer_api.domain.model.UserRegisterResult

interface AuthRemoteDataSourceI {
    suspend fun login(data: UserLoginBody): UserLoginResult
    suspend fun register(data: UserRegisterBody): UserRegisterResult
}
