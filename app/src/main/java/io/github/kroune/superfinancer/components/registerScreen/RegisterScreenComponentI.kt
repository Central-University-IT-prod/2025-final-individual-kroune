package io.github.kroune.superfinancer.components.registerScreen

import io.github.kroune.super_financer_api.domain.model.UserRegisterResult
import io.github.kroune.superfinancer.events.RegisterScreenEvent

interface RegisterScreenComponentI {
    val username: String
    val usernameValid: Boolean
    val password: String
    val passwordValid: Boolean
    val registerResult: UserRegisterResult?
    val registerInProcess: Boolean
    fun onEvent(event: RegisterScreenEvent)
}
