package io.github.kroune.super_financer_ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun SuperFinancerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        CompositionLocalProvider(
            LocalFixedAccentColors provides getFixedAccentColors(),
            LocalFixedSpacing provides getScreenSpacing()
        ) {
            content()
        }
    }
}

object SuperFinancer {
    val fixedAccentColors: FixedAccentColors
        @Composable
        get() = LocalFixedAccentColors.current

    val screenSpacing: MainScreenSpacing
        @Composable
        get() = LocalFixedSpacing.current
}

data class FixedAccentColors(
    val lossColor: Color,
    val gainColor: Color,
    val errorColor: Color,
    val successColor: Color,
    val linkColor: Color
)

data class MainScreenSpacing(
    val defaultHorizontalSpacing: Dp
)

fun getFixedAccentColors() = FixedAccentColors(
    lossColor = Color.Red,
    gainColor = Color.Green,
    errorColor = Color.Red,
    successColor = Color.Green,
    linkColor = Color(0.49f, 0.639f, 0.878f, 1.0f)
)

fun getScreenSpacing() = MainScreenSpacing(
    10.dp
)

val LocalFixedAccentColors = compositionLocalOf {
    getFixedAccentColors()
}

val LocalFixedSpacing = compositionLocalOf {
    getScreenSpacing()
}
