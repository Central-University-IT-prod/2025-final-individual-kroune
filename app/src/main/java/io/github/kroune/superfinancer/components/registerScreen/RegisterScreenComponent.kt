package io.github.kroune.superfinancer.components.registerScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.super_financer_api.domain.model.UserRegisterBody
import io.github.kroune.super_financer_api.domain.model.UserRegisterResult
import io.github.kroune.superfinancer.domain.repositories.authRepository.AuthRepositoryI
import io.github.kroune.superfinancer.domain.usecases.authDataValidator.AuthDataValidatorI
import io.github.kroune.superfinancer.events.RegisterScreenEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RegisterScreenComponent(
    val onSuccessfulAuth: () -> Unit,
    val onNavigateToLoginScreen: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, RegisterScreenComponentI, KoinComponent {
    override var username by mutableStateOf("")
    override var usernameValid by mutableStateOf(false)
    override var password by mutableStateOf("")
    override var passwordValid by mutableStateOf(false)

    override var registerResult: UserRegisterResult? by mutableStateOf(
        null,
        policy = referentialEqualityPolicy()
    )
    override var registerInProcess by mutableStateOf(false)

    private val authRepository by inject<AuthRepositoryI>()
    private val authDataValidator by inject<AuthDataValidatorI>()

    override fun onEvent(event: RegisterScreenEvent) {
        when (event) {
            RegisterScreenEvent.LogIn -> {
                registerInProcess = true
                CoroutineScope(Dispatchers.Default).launch {
                    registerResult = authRepository.register(UserRegisterBody(username, password))
                    registerInProcess = false
                    if (registerResult is UserRegisterResult.Success) {
                        onSuccessfulAuth()
                    }
                }
            }

            RegisterScreenEvent.NavigateToLoginScreen -> {
                onNavigateToLoginScreen()
            }

            is RegisterScreenEvent.UsernameUpdate -> {
                username = event.newText
                usernameValid = authDataValidator.loginValidator(event.newText)
            }

            is RegisterScreenEvent.PasswordUpdate -> {
                password = event.newText
                passwordValid = authDataValidator.passwordValidator(event.newText)
            }
        }
    }
}
