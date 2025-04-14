package io.github.kroune.superfinancer.components.mainScreen

import org.intellij.lang.annotations.MagicConstant
import kotlin.time.Duration.Companion.minutes

object MainScreenConstants {
    @MagicConstant
    val timeBeforeRefreshingTickers = 5.minutes
    @MagicConstant
    val tickerToLoadWhenSearching = 10
}
