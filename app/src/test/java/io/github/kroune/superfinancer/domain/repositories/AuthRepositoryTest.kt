package io.github.kroune.superfinancer.domain.repositories

import io.github.kroune.super_financer_api.data.sources.remote.superFinancer.authRemoteDataSource.AuthRemoteDataSourceI
import io.github.kroune.super_financer_api.domain.model.UserLoginBody
import io.github.kroune.super_financer_api.domain.model.UserLoginResult
import io.github.kroune.super_financer_api.domain.model.UserRegisterBody
import io.github.kroune.super_financer_api.domain.model.UserRegisterResult
import io.github.kroune.superfinancer.data.sources.local.superFinancer.jwtTokenLocalDataSource.JwtTokenLocalDataSourceI
import io.github.kroune.superfinancer.domain.repositories.authRepository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthRepositoryTest {

    private lateinit var module: Module

    @Before
    fun setUp() {
        module = module {
            single<AuthRemoteDataSourceI> { mockRemote }
            single<JwtTokenLocalDataSourceI> { mockLocal }
        }
        startKoin {
            modules(module)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    private val mockRemote = object : AuthRemoteDataSourceI {
        override suspend fun login(data: UserLoginBody): UserLoginResult {
            return if (data.login == "valid_user") {
                UserLoginResult.Success("valid_token")
            } else {
                UserLoginResult.InvalidCredentials
            }
        }

        override suspend fun register(data: UserRegisterBody): UserRegisterResult {
            if (data.login == "conflict")
                return UserRegisterResult.LoginConflict
            return UserRegisterResult.Success("new_token")
        }
    }

    private val mockLocal = object : JwtTokenLocalDataSourceI {
        var token: String? = null
        private val _jwtTokenState = MutableStateFlow<String?>(null)

        override suspend fun updateJwtToken(token: String) {
            this.token = token
            _jwtTokenState.emit(token)
        }

        override suspend fun removeJwtToken() {
            token = null
            _jwtTokenState.emit(null)
        }

        override fun getJwtToken(): String? = token

        override val jwtTokenState: StateFlow<String?>
            get() = _jwtTokenState
    }

    @Test
    fun `should update JWT token on successful registration`() {
        val repository = AuthRepository()

        val result = runBlocking { repository.register(UserRegisterBody("user", "password")) }

        assertTrue { result is UserRegisterResult.Success }
        assertEquals("new_token", mockLocal.getJwtToken())
    }
    @Test
    fun `should not update JWT token on failed registration`() {
        val repository = AuthRepository()

        runBlocking {
            mockLocal.updateJwtToken("some_token")
        }
        val result = runBlocking { repository.register(UserRegisterBody("conflict", "password")) }

        assertTrue { result is UserRegisterResult.LoginConflict }
        assertNotEquals("new_token", mockLocal.getJwtToken())
    }

    @Test
    fun `should remove JWT token`() {
        val repository = AuthRepository()
        mockLocal.token = "existing_token"

        runBlocking { repository.removeJwtToken() }

        assertEquals(null, mockLocal.getJwtToken())
    }

    @Test
    fun `should update JWT token on successful login`() {
        val repository = AuthRepository()

        val result = runBlocking { repository.login(UserLoginBody("valid_user", "password")) }

        assertTrue { result is UserLoginResult.Success }
        assertEquals("valid_token", mockLocal.getJwtToken())
    }

    @Test
    fun `should not update JWT token on failed login`() {
        val repository = AuthRepository()

        val result = runBlocking { repository.login(UserLoginBody("invalid_user", "password")) }

        assertTrue { result is UserLoginResult.InvalidCredentials }
        assertEquals(null, mockLocal.getJwtToken())
    }

    @Test
    fun `should get JWT token state`() {
        val repository = AuthRepository()
        mockLocal.token = "existing_token"

        assertEquals(repository.jwtTokenState.value, mockLocal.jwtTokenState.value)
    }

    @Test
    fun `should get JWT token`() {
        val repository = AuthRepository()
        runBlocking {
            mockLocal.updateJwtToken("existing_token")
        }

        assertEquals(repository.jwtTokenState.value, repository.getJwtToken())
        assertEquals(mockLocal.getJwtToken(), repository.getJwtToken())
        val newToken = "new token"
        runBlocking {
            repository.updateJwtToken(newToken)
        }
        assertEquals(newToken, repository.getJwtToken())
    }
}
