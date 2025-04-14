package io.github.kroune.superfinancer.ui.loginScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.super_financer_api.domain.model.UserLoginResult
import io.github.kroune.super_financer_ui.theme.SuperFinancer
import io.github.kroune.superfinancer.R
import io.github.kroune.superfinancer.components.loginScreen.LoginScreenComponentI
import io.github.kroune.superfinancer.events.LoginScreenEvent
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(component: LoginScreenComponentI) {
    val username = component.username
    val isUsernameValid = component.usernameValid
    val password = component.password
    val isPasswordValid = component.passwordValid
    val requestInProcess = component.loginInProcess
    val loginResult = component.loginResult
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(10f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    username,
                    { newValue ->
                        component.onEvent(LoginScreenEvent.UsernameUpdate(newValue))
                    },
                    label = {
                        if (!isUsernameValid && username.isNotEmpty()) {
                            Text(
                                stringResource(R.string.invalid_login),
                                modifier = Modifier,
                                color = Color.Red,
                                fontSize = 12.sp
                            )
                        }
                    },
                    isError = !isUsernameValid && username.isNotEmpty(),
                    placeholder = { Text(stringResource(R.string.login)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.username),
                            "your username"
                        )
                    },
                    modifier = Modifier
                        .padding(10.dp)
                )
                TextField(
                    password,
                    { newValue ->
                        component.onEvent(LoginScreenEvent.PasswordUpdate(newValue))
                    },
                    label = {
                        if (!isPasswordValid && password.isNotEmpty()) {
                            Text(
                                stringResource(R.string.invalid_password),
                                modifier = Modifier,
                                color = Color.Red,
                                fontSize = 12.sp
                            )
                        }
                    },
                    isError = !isPasswordValid && password.isNotEmpty(),
                    placeholder = { Text(stringResource(R.string.password)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.password),
                            "your password"
                        )
                    },
                    modifier = Modifier
                        .padding(10.dp)
                )
                Button(
                    modifier = Modifier
                        .padding(20.dp),
                    onClick = {
                        component.onEvent(LoginScreenEvent.LogIn)
                    },
                    enabled = isUsernameValid && isPasswordValid && !requestInProcess
                ) {
                    Text(stringResource(R.string.log_in))
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.no_account_question_mark))
                    TextButton(
                        onClick = {
                            component.onEvent(LoginScreenEvent.NavigateToRegisterScreen)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = SuperFinancer.fixedAccentColors.linkColor
                        )
                    ) {
                        Text(stringResource(R.string.register))
                    }
                }
            }
        }
    }

    val text = when (loginResult) {
        UserLoginResult.InvalidCredentials -> {
            stringResource(R.string.login_unautorized)
        }

        UserLoginResult.UnknownError -> {
            stringResource(R.string.login_unknown_error)
        }

        UserLoginResult.TooManyRequests -> {
            stringResource(R.string.login_too_many_requests)
        }

        UserLoginResult.NetworkError -> {
            stringResource(R.string.login_network_error)
        }

        UserLoginResult.ServerError -> {
            stringResource(R.string.login_server_error)
        }

        is UserLoginResult.Success, null -> return
    }
    LaunchedEffect(loginResult) {
        scope.launch {
            snackbarHostState.showSnackbar(text)
        }
    }
}
