package io.github.kroune.superfinancer.events

sealed interface RegisterScreenEvent {
    data object LogIn: RegisterScreenEvent
    data object NavigateToLoginScreen: RegisterScreenEvent
    data class UsernameUpdate(val newText: String): RegisterScreenEvent
    data class PasswordUpdate(val newText: String): RegisterScreenEvent
}
