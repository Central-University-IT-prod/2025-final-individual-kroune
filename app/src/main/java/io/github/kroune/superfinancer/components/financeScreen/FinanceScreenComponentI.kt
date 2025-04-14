package io.github.kroune.superfinancer.components.financeScreen

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import io.github.kroune.superfinancer.domain.models.database.FinanceGoalEntity
import io.github.kroune.superfinancer.events.FinanceScreenEvent
import io.github.kroune.superfinancer.ui.financeScreen.OperationsUiModel

interface FinanceScreenComponentI {
    val totalMoney: Float?
    fun onEvent(event: FinanceScreenEvent)
    val operations: SnapshotStateList<OperationsUiModel>
    val goals: SnapshotStateList<Pair<FinanceGoalEntity, Float>>
    val totalGoalCost: Float?

    val goalTitle: String
    val isGoalTitleValid: Boolean
    val goalCost: String
    val isGoalCostValid: Boolean

    val pieChartData: SnapshotStateMap<String, Float>
    val pieChartSum: Float
}
