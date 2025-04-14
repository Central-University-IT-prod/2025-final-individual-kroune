package io.github.kroune.superfinancer.components.loginScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.super_financer_api.domain.model.UserLoginBody
import io.github.kroune.super_financer_api.domain.model.UserLoginResult
import io.github.kroune.superfinancer.domain.repositories.authRepository.AuthRepositoryI
import io.github.kroune.superfinancer.domain.usecases.authDataValidator.AuthDataValidatorI
import io.github.kroune.superfinancer.events.LoginScreenEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginScreenComponent(
    val onSuccessfulAuth: () -> Unit,
    val onNavigateToRegisterScreen: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, LoginScreenComponentI, KoinComponent {
    override var username by mutableStateOf("")
    override var usernameValid by mutableStateOf(false)
    override var password by mutableStateOf("")
    override var passwordValid by mutableStateOf(false)

    override var loginResult: UserLoginResult? by mutableStateOf(
        null,
        policy = referentialEqualityPolicy()
    )
    override var loginInProcess by mutableStateOf(false)

    private val authRepository by inject<AuthRepositoryI>()
    private val authDataValidator by inject<AuthDataValidatorI>()

    private fun login() {
        CoroutineScope(Dispatchers.Default).launch {
            loginInProcess = true
            loginResult = authRepository.login(UserLoginBody(username, password))
            loginInProcess = false
            if (loginResult is UserLoginResult.Success) {
                withContext(Dispatchers.Main.immediate) {
                    onSuccessfulAuth()
                }
            }
        }
    }

    override fun onEvent(event: LoginScreenEvent) {
        when (event) {
            LoginScreenEvent.LogIn -> {
                login()
            }

            LoginScreenEvent.NavigateToRegisterScreen -> {
                onNavigateToRegisterScreen()
            }

            is LoginScreenEvent.UsernameUpdate -> {
                username = event.newText
                usernameValid = authDataValidator.loginValidator(event.newText)
            }

            is LoginScreenEvent.PasswordUpdate -> {
                password = event.newText
                passwordValid = authDataValidator.passwordValidator(event.newText)
            }
        }
    }
}
