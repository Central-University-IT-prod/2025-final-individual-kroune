package io.github.kroune.superfinancer.ui.registerScreen

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
import io.github.kroune.super_financer_api.domain.model.UserRegisterResult
import io.github.kroune.super_financer_ui.theme.SuperFinancer
import io.github.kroune.superfinancer.R
import io.github.kroune.superfinancer.components.registerScreen.RegisterScreenComponentI
import io.github.kroune.superfinancer.events.RegisterScreenEvent
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(component: RegisterScreenComponentI) {
    val username = component.username
    val isUsernameValid = component.usernameValid
    val password = component.password
    val isPasswordValid = component.passwordValid
    val requestInProcess = component.registerInProcess
    val registerResult = component.registerResult
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
                        component.onEvent(RegisterScreenEvent.UsernameUpdate(newValue))
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
                        component.onEvent(RegisterScreenEvent.PasswordUpdate(newValue))
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
                        component.onEvent(RegisterScreenEvent.LogIn)
                    },
                    enabled = isUsernameValid && isPasswordValid && !requestInProcess
                ) {
                    Text(stringResource(R.string.register))
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
                    Text(stringResource(R.string.have_an_account_question_mark))
                    TextButton(
                        onClick = {
                            component.onEvent(RegisterScreenEvent.NavigateToLoginScreen)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = SuperFinancer.fixedAccentColors.linkColor
                        )
                    ) {
                        Text(stringResource(R.string.log_in))
                    }
                }
            }
        }
    }

    val text = when (registerResult) {
        UserRegisterResult.LoginConflict -> {
            stringResource(R.string.register_conflict)
        }

        UserRegisterResult.UnknownError -> {
            stringResource(R.string.register_unknown_error)
        }

        UserRegisterResult.TooManyRequests -> {
            stringResource(R.string.register_too_many_requests)
        }

        UserRegisterResult.NetworkError -> {
            stringResource(R.string.register_network_error)
        }

        UserRegisterResult.ServerError -> {
            stringResource(R.string.register_server_error)
        }

        is UserRegisterResult.Success, null -> return
    }
    LaunchedEffect(registerResult) {
        scope.launch {
            snackbarHostState.showSnackbar(text)
        }
    }
}
