package io.github.kroune.superfinancer.domain.usecases

import org.intellij.lang.annotations.MagicConstant

object FinanceDataValidatorConstants {
    @MagicConstant
    val validTitleLength = 1..30
    @MagicConstant
    val maxCost = 1_000_000_000_000_000L
}
