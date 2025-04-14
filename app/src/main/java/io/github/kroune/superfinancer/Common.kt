package io.github.kroune.superfinancer

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.isFinished
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.animation.Direction
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.math.abs

fun ComponentContext.componentCoroutineScope(): CoroutineScope {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    if (lifecycle.state != Lifecycle.State.DESTROYED) {
        lifecycle.doOnDestroy {
            scope.cancel()
        }
    } else {
        scope.cancel()
    }
    return scope
}

class CustomStackAnimator(
    private val animationSpec: FiniteAnimationSpec<Float> = tween(),
    private val frame: @Composable (
        factor: Float, direction: Direction, content: @Composable (Modifier) -> Unit
    ) -> Unit,
    private val invertDirection: Boolean = false
) : StackAnimator {

    @Composable
    override operator fun invoke(
        direction: Direction,
        isInitial: Boolean,
        onFinished: () -> Unit,
        content: @Composable (Modifier) -> Unit,
    ) {
        val animationState = remember(
            direction, isInitial
        ) { AnimationState(initialValue = if (isInitial) 0F else 1F) }

        LaunchedEffect(animationState) {
            animationState.animateTo(
                targetValue = 0F,
                animationSpec = animationSpec,
                sequentialAnimation = !animationState.isFinished,
            )

            onFinished()
        }

        val factor = when (direction) {
            Direction.ENTER_FRONT -> animationState.value
            Direction.EXIT_FRONT -> 1F - animationState.value
            Direction.ENTER_BACK -> -animationState.value
            Direction.EXIT_BACK -> animationState.value - 1F
        } * if (invertDirection) -1 else 1

        frame(factor, direction, content)
    }
}

/**
 * A simple sliding animation. Children enter from one side and exit to another side.
 */
fun customSlide(
    animationSpec: FiniteAnimationSpec<Float> = tween(),
    orientation: Orientation = Orientation.Horizontal,
    invertDirection: Boolean = false
): StackAnimator = CustomStackAnimator(
    animationSpec = animationSpec, { factor, _, content ->
        content(
            when (orientation) {
                Orientation.Horizontal -> Modifier.offsetXFactor(factor)
                Orientation.Vertical -> Modifier.offsetYFactor(factor)
            }
        )
    }, invertDirection = invertDirection
)


private fun Modifier.offsetXFactor(factor: Float): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)

    layout(placeable.width, placeable.height) {
        placeable.placeRelative(x = (placeable.width.toFloat() * factor).toInt(), y = 0)
    }
}

private fun Modifier.offsetYFactor(factor: Float): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)

    layout(placeable.width, placeable.height) {
        placeable.placeRelative(x = 0, y = (placeable.height.toFloat() * factor).toInt())
    }
}

val Color.Companion.LightGreen: Color
    get() {
        return Color(0.518f, 1.0f, 0.431f, 1.0f)
    }


@Composable
fun PieChart(
    title: String,
    totalSum: Float,
    data: SnapshotStateMap<String, Float> = mutableStateMapOf(),
    legendTransformation: (String) -> String = { it },
    costTransformation: (Float) -> String = { it.toString() },
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 30.dp,
    diameter: Dp = 90.dp
) {
    if (abs(totalSum) < 0.01f) return
    val colorsPool = remember {
        listOf(
            Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta
        )
    }
    val stroke =
        with(LocalDensity.current) { Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt) }
    Column {
        Text(
            title, fontWeight = FontWeight.SemiBold, fontSize = 20.sp
        )
        Spacer(Modifier.height(10.dp))
        Row {
            Canvas(
                modifier.size(diameter)
            ) {
                var currentAngle = 270f
                var index = 0
                data.forEach { (name, value) ->
                    val curProgress = (value / totalSum).coerceIn(0f, 1f)
                    val sweep = curProgress * 360f
                    drawDeterminateCircularIndicator(
                        currentAngle,
                        sweep,
                        colorsPool[abs(name.hashCode()) % colorsPool.size],
                        stroke
                    )
                    currentAngle += sweep
                    index++
                }
            }
            Spacer(Modifier.width(10.dp))
            Card {
                Row(
                    Modifier.padding(10.dp)
                ) {
                    Column {
                        data.forEach { (name, _) ->
                            Text(
                                legendTransformation(name),
                                color = colorsPool[abs(name.hashCode()) % colorsPool.size]
                            )
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        data.forEach { (_, value) ->
                            Text(costTransformation(value))
                        }
                    }
                }
            }
        }
    }
}


private fun DrawScope.drawCircularIndicator(
    startAngle: Float, sweep: Float, color: Color, stroke: Stroke
) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}


private fun DrawScope.drawDeterminateCircularIndicator(
    startAngle: Float, sweep: Float, color: Color, stroke: Stroke
) = drawCircularIndicator(startAngle, sweep, color, stroke)
