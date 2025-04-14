package io.github.kroune.superfinancer.domain.repositories.authRepository

import io.github.kroune.super_financer_api.domain.model.UserLoginBody
import io.github.kroune.super_financer_api.domain.model.UserLoginResult
import io.github.kroune.super_financer_api.domain.model.UserRegisterBody
import io.github.kroune.super_financer_api.domain.model.UserRegisterResult
import kotlinx.coroutines.flow.StateFlow

interface AuthRepositoryI {
    suspend fun login(data: UserLoginBody): UserLoginResult
    suspend fun register(data: UserRegisterBody): UserRegisterResult
    suspend fun updateJwtToken(token: String)
    suspend fun removeJwtToken()
    fun getJwtToken(): String?
    val jwtTokenState: StateFlow<String?>
}
