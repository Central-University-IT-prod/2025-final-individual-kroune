package io.github.kroune.superfinancer.data.sources.local.superFinancer.jwtTokenLocalDataSource

import kotlinx.coroutines.flow.StateFlow

interface JwtTokenLocalDataSourceI {
    suspend fun updateJwtToken(token: String)
    suspend fun removeJwtToken()
    fun getJwtToken(): String?
    val jwtTokenState: StateFlow<String?>
}
