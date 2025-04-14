package io.github.kroune.superfinancer.components.loginScreen

import io.github.kroune.super_financer_api.domain.model.UserLoginResult
import io.github.kroune.superfinancer.events.LoginScreenEvent

interface LoginScreenComponentI {
    val username: String
    val usernameValid: Boolean
    val password: String
    val passwordValid: Boolean
    val loginResult: UserLoginResult?
    val loginInProcess: Boolean
    fun onEvent(event: LoginScreenEvent)
}
