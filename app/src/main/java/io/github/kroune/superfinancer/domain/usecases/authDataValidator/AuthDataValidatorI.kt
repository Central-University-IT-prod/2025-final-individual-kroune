package io.github.kroune.superfinancer.domain.usecases.authDataValidator

interface AuthDataValidatorI {
    fun loginValidator(username: String): Boolean
    fun passwordValidator(password: String): Boolean
}
