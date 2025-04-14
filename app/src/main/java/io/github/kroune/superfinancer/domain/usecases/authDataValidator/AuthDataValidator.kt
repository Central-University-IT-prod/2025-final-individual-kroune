package io.github.kroune.superfinancer.domain.usecases.authDataValidator

class AuthDataValidator : AuthDataValidatorI {
    override fun loginValidator(username: String): Boolean {
        return username.all { it.isLetterOrDigit() }
                && username.length in AuthDataValidatorConstants.loginLength
    }

    override fun passwordValidator(password: String): Boolean {
        return password.all { (it.isLetterOrDigit() || it == '!' || it == '.' || it == '?') }
                && password.length in AuthDataValidatorConstants.passwordLength
    }
}
