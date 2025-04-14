package io.github.kroune.superfinancer.ui.financeScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.kroune.superfinancer.LightGreen
import io.github.kroune.superfinancer.R
import io.github.kroune.superfinancer.domain.models.database.FinanceGoalEntity


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawFinanceGoal(goal: FinanceGoalEntity, currentProgress: Float, onDelete: () -> Unit) {
    val showDeletionDialog = remember { MutableTransitionState(false) }
    Card(
        modifier = Modifier
            .zIndex(2f)
            .combinedClickable(
                onLongClick = {
                    showDeletionDialog.targetState = true
                },
                onClick = {
                    showDeletionDialog.targetState = false
                },
            )
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    goal.name,
                    modifier = Modifier
                        .weight(1f, false)
                )
                Spacer(Modifier.width(10.dp))
                Row(
                    modifier = Modifier
                        .weight(1f, false),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${
                            "%.2f".format(currentProgress).removeSuffix(".00")
                        }/${
                            "%.2f".format(goal.amount).removeSuffix(".00")
                        }"
                    )
                    AnimatedVisibility(
                        showDeletionDialog,
                        Modifier.requiredSize(20.dp)
                    ) {
                        IconButton(
                            {
                                onDelete()
                            },
                        ) {
                            Icon(
                                painterResource(R.drawable.close), "remove objective"
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(5.dp))
            val progress = (currentProgress / goal.amount).coerceAtMost(1f)
            val animatedColor by animateColorAsState(
                when (progress) {
                    in Float.NEGATIVE_INFINITY..0.3f -> {
                        Color.Yellow
                    }

                    in 0.3f..0.6f -> {
                        Color.LightGreen
                    }

                    in 0.6f..Float.POSITIVE_INFINITY -> {
                        Color.Green
                    }

                    else -> error("")
                },
                label = "progress color"
            )
            LinearProgressIndicator(
                { progress },
                modifier = Modifier.fillMaxWidth(),
                drawStopIndicator = {},
                color = animatedColor,
                gapSize = 0.dp
            )
        }
    }
}
