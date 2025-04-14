package io.github.kroune.superfinancer.ui.financeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.kroune.superfinancer.R

@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    addGoal: (goalName: String, cost: Float) -> Unit,
    goalTitle: String,
    updateGoalTitle: (goalTitle: String) -> Unit,
    isGoalTitleValid: Boolean,
    goalCost: String,
    updateGoalCost: (goalText: String) -> Unit,
    isGoalCostValid: Boolean
) {
    Dialog(
        onDismiss,
    ) {
        Card(
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    goalTitle,
                    { updateGoalTitle(it) },
                    placeholder = {
                        Text(stringResource(R.string.goal_name))
                    },
                    singleLine = true,
                    maxLines = 1,
                    isError = !isGoalTitleValid && goalTitle.isNotEmpty()
                )
                OutlinedTextField(
                    goalCost,
                    {
                        updateGoalCost(it)
                    },
                    placeholder = {
                        Text(stringResource(R.string.cost))
                    },
                    isError = !isGoalCostValid && goalCost.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Button(
                    {
                        addGoal(goalTitle, goalCost.toFloat())
                        onDismiss()
                    },
                    enabled = isGoalCostValid && isGoalTitleValid
                ) {
                    Text(stringResource(R.string.add))
                }
            }
        }
    }
}
