package io.github.kroune.superfinancer.ui.financeScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.kroune.superfinancer.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawFinanceOperation(operation: OperationsUiModel, onDelete: () -> Unit) {
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
                    when (operation.amount) {
                        in Float.NEGATIVE_INFINITY..<0f -> {
                            stringResource(R.string.subtraction)
                        }

                        in 0f..Float.POSITIVE_INFINITY -> {
                            stringResource(R.string.addition)
                        }

                        else -> {
                            error("")
                        }
                    },
                    Modifier.weight(1f, false)
                )
                Spacer(Modifier.width(10.dp))
                Row(
                    modifier = Modifier
                        .weight(1f, false),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "%.2f".format(operation.amount)
                            .removeSuffix(".00") + " " + stringResource(R.string.currency)
                    )
                    AnimatedVisibility(showDeletionDialog, Modifier.requiredSize(20.dp)) {
                        IconButton({
                            onDelete()
                        }) {
                            Icon(painterResource(R.drawable.close), "remove objective")
                        }
                    }
                }
            }
            val goalName = operation.goalName
            Text(goalName)
            val operationComment = operation.comment
            if (operationComment != null)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    )
                ) {
                    Text(
                        operationComment,
                        modifier = Modifier.padding(10.dp)
                    )
                }
        }
    }
}
