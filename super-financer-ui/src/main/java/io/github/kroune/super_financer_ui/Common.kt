package io.github.kroune.super_financer_ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * a simple infinite loading animation
 */
@Composable
fun LoadingCircle(
    modifier: Modifier = Modifier,
    durationMillis: Int = 1000
) {
    val animatedProgress by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis, easing = LinearEasing
            ), repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    CircularProgressIndicator(
        progress = { animatedProgress },
        color = Color.Black,
        modifier = modifier
            .aspectRatio(1f)
    )
}
