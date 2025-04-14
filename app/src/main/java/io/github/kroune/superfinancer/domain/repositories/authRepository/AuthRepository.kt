package io.github.kroune.superfinancer.domain.repositories.authRepository

import io.github.kroune.super_financer_api.data.sources.remote.superFinancer.authRemoteDataSource.AuthRemoteDataSourceI
import io.github.kroune.super_financer_api.domain.model.UserLoginBody
import io.github.kroune.super_financer_api.domain.model.UserLoginResult
import io.github.kroune.super_financer_api.domain.model.UserRegisterBody
import io.github.kroune.super_financer_api.domain.model.UserRegisterResult
import io.github.kroune.superfinancer.data.sources.local.superFinancer.jwtTokenLocalDataSource.JwtTokenLocalDataSourceI
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthRepository : AuthRepositoryI, KoinComponent {
    private val remote by inject<AuthRemoteDataSourceI>()
    private val local by inject<JwtTokenLocalDataSourceI>()
    override suspend fun login(data: UserLoginBody): UserLoginResult {
        val result = remote.login(data)
        if (result is UserLoginResult.Success) {
            local.updateJwtToken(result.token)
        }
        return result
    }

    override suspend fun register(data: UserRegisterBody): UserRegisterResult {
        val result = remote.register(data)
        if (result is UserRegisterResult.Success) {
            local.updateJwtToken(result.token)
        }
        return result
    }

    override suspend fun updateJwtToken(token: String) {
        local.updateJwtToken(token)
    }

    override suspend fun removeJwtToken() {
        local.removeJwtToken()
    }

    override fun getJwtToken(): String? {
        return local.getJwtToken()
    }

    override val jwtTokenState: StateFlow<String?>
        get() = local.jwtTokenState
}
