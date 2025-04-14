package io.github.kroune.superfinancer.domain.usecases.authDataValidator

import org.intellij.lang.annotations.MagicConstant

object AuthDataValidatorConstants {
    @MagicConstant
    val loginLength = 3..50
    @MagicConstant
    val passwordLength = 6..50
}
