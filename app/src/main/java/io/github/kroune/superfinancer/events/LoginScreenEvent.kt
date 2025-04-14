package io.github.kroune.superfinancer.events

sealed interface LoginScreenEvent {
    data object LogIn: LoginScreenEvent
    data object NavigateToRegisterScreen: LoginScreenEvent
    data class UsernameUpdate(val newText: String): LoginScreenEvent
    data class PasswordUpdate(val newText: String): LoginScreenEvent
}
