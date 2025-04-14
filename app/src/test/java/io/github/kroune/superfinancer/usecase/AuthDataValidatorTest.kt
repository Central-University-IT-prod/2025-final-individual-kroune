package io.github.kroune.superfinancer.usecase

import io.github.kroune.superfinancer.domain.usecases.authDataValidator.AuthDataValidator
import org.junit.Test

class AuthDataValidatorTest {
    private val authDataValidator = AuthDataValidator()

    @Test
    fun loginValidatorInvalidWithSpace() {
        assert(!authDataValidator.loginValidator("user name"))
        assert(!authDataValidator.loginValidator("someBiggeruser name"))
        assert(!authDataValidator.loginValidator("us "))
    }

    @Test
    fun loginValidatorInvalidWithNotLetterOrDigits() {
        assert(!authDataValidator.loginValidator("\\sqp``"))
        assert(!authDataValidator.loginValidator("```````"))
        assert(!authDataValidator.loginValidator("\\\\\\\""))
    }

    @Test
    fun loginValidatorTooShort() {
        assert(!authDataValidator.loginValidator("dj"))
        assert(!authDataValidator.loginValidator("d1"))
        assert(!authDataValidator.loginValidator("q"))
        assert(!authDataValidator.loginValidator(""))
    }

    @Test
    fun loginValidatorValid() {
        assert(authDataValidator.loginValidator("someTestName"))
        assert(authDataValidator.loginValidator("someTestName123"))
        assert(authDataValidator.loginValidator("normaluser"))
        assert(authDataValidator.loginValidator("anotheruser100"))
    }

    @Test
    fun loginValidatorTooLong() {
        assert(!authDataValidator.loginValidator("adrjwinsidashduahsdihaidsadjdrjwinshduahsdihaidsadj"))
        assert(!authDataValidator.loginValidator("adrjwinsidashduahsdihaidsadjdrjwinshduahsdihaiasdasd31231dsadj"))
        assert(!authDataValidator.loginValidator("adrjwinsidashduahsdihaidsadjdrjwinshduahsdih2idsadj"))
    }

    @Test
    fun passwordValidatorInvalidWithSpace() {
        assert(!authDataValidator.passwordValidator("user name"))
        assert(!authDataValidator.passwordValidator("someBiggeruser name"))
        assert(!authDataValidator.passwordValidator("us "))
    }

    @Test
    fun passwordValidatorValid() {
        assert(authDataValidator.passwordValidator("ThisIsSecure45"))
        assert(authDataValidator.passwordValidator("amothersecurepass!"))
        assert(authDataValidator.passwordValidator("yepsecureT.oo"))
        assert(authDataValidator.passwordValidator("yepsecureT?oo"))
    }

    @Test
    fun passwordValidatorTooShort() {
        assert(!authDataValidator.passwordValidator("dj428"))
        assert(!authDataValidator.passwordValidator("d11s"))
        assert(!authDataValidator.passwordValidator("q"))
        assert(!authDataValidator.passwordValidator(""))
    }

    @Test
    fun passwordValidatorTooLong() {
        assert(!authDataValidator.passwordValidator("adrjwinsidashduahsdihaidsadjdrjwinshduahsdihaidsadj"))
        assert(!authDataValidator.passwordValidator("adrjwinsidashduahsdihaidsadjdrjwinshduahsdihaiasdasd31231dsadj"))
        assert(!authDataValidator.passwordValidator("adrjwinsidashduahsdihaidsadjdrjwinshduahsdih2idsadj"))
    }
}
