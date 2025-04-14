package io.github.kroune.superfinancer.ui.financeScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.github.kroune.superfinancer.PieChart
import io.github.kroune.superfinancer.R
import io.github.kroune.superfinancer.components.financeScreen.FinanceScreenComponentI
import io.github.kroune.superfinancer.events.FinanceScreenEvent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FinanceScreen(
    component: FinanceScreenComponentI
) {
    var showAddOperationDialog by rememberSaveable { mutableStateOf(false) }
    var showAddGoalDialog by rememberSaveable { mutableStateOf(false) }
    val goals = component.goals
    LazyColumn(
        modifier = Modifier
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        stickyHeader {
            Card(
                modifier = Modifier.zIndex(3f),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Column(
                    Modifier
                        .padding(10.dp)
                ) {
                    Text(
                        stringResource(R.string.finance_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                    val totalMoney = component.totalMoney
                    val totalGoalMoney = component.totalGoalCost
                    if (totalMoney != null && totalGoalMoney != null && totalGoalMoney != 0f) {
                        Text(
                            "${
                                "%.2f".format(totalMoney).removeSuffix(".00")
                            }/${
                                "%.2f".format(totalGoalMoney).removeSuffix(".00")
                            } ${stringResource(R.string.in_total)}"
                        )
                        Text(
                            "${
                                "%.2f".format(totalMoney.toFloat() / totalGoalMoney)
                                    .removeSuffix(".00")
                            } %"
                        )
                    }
                }
            }
        }
        val totalMoney = component.totalMoney
        if (totalMoney != null)
            item {
                Row {
                    val currencySign = stringResource(R.string.currency)
                    PieChart(
                        stringResource(R.string.gains_distribution),
                        component.pieChartSum,
                        component.pieChartData,
                        { it },
                        {
                            "%.2f".format(it).removeSuffix(".00") + " " + currencySign
                        }
                    )
                }
            }
        item {
            TextButton(
                {
                    showAddGoalDialog = true
                }
            ) {
                Text(stringResource(R.string.add_goal))
            }
        }
        items(goals.size, { "goal-$it" }) {
            val goal = goals[it]
            DrawFinanceGoal(
                goal.first,
                goal.second
            ) {
                component.onEvent(FinanceScreenEvent.OnGoalDeletion(goal.first.id))
            }
        }
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 2.dp)
        }
        item {
            TextButton(
                {
                    showAddOperationDialog = true
                }
            ) {
                Text(stringResource(R.string.add_operation))
            }
        }
        val operations = component.operations
        items(operations.size, { "operation-$it" }) {
            DrawFinanceOperation(
                operations[it]
            ) {
                component.onEvent(FinanceScreenEvent.OnOperationDeletion(operations[it].id))
            }
        }
    }
    if (showAddOperationDialog) {
        AddOperationDialog(
            { showAddOperationDialog = false },
            { amount, goalId, comment ->
                component.onEvent(
                    FinanceScreenEvent.AddOperation(
                        amount,
                        goalId,
                        comment
                    )
                )
            },
            goals
        )
    }
    if (showAddGoalDialog) {
        AddGoalDialog(
            { showAddGoalDialog = false },
            { goalName, cost ->
                component.onEvent(FinanceScreenEvent.AddGoal(goalName, cost))
            },
            component.goalTitle,
            {
                component.onEvent(FinanceScreenEvent.GoalTitleUpdate(it))
            },
            component.isGoalTitleValid,
            component.goalCost,
            {
                component.onEvent(FinanceScreenEvent.GoalCostUpdate(it))
            },
            component.isGoalCostValid
        )
    }
}
