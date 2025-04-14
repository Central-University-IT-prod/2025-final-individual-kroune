package io.github.kroune.superfinancer.components.financeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.superfinancer.componentCoroutineScope
import io.github.kroune.superfinancer.domain.models.database.FinanceGoalEntity
import io.github.kroune.superfinancer.domain.models.database.FinanceOperationEntity
import io.github.kroune.superfinancer.domain.repositories.financeOperationsRepository.FinanceOperationsRepositoryI
import io.github.kroune.superfinancer.domain.repositories.financerGoalsRepository.FinanceGoalsRepositoryI
import io.github.kroune.superfinancer.domain.usecases.FinanceDataValidatorI
import io.github.kroune.superfinancer.events.FinanceScreenEvent
import io.github.kroune.superfinancer.ui.financeScreen.OperationsUiModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FinanceScreenComponent(
    val onNavigationToMainScreen: () -> Unit,
    val onNavigationToNewsFeedScreen: () -> Unit,
    componentContext: ComponentContext,
) : ComponentContext by componentContext, FinanceScreenComponentI, KoinComponent {
    private val componentScope = componentCoroutineScope()
    override var totalGoalCost: Float? by mutableStateOf(null)
    override var goalTitle: String by mutableStateOf("")
    override var isGoalTitleValid: Boolean by mutableStateOf(false)
    override var goalCost: String by mutableStateOf("")
    override var isGoalCostValid: Boolean by mutableStateOf(false)
    override var totalMoney: Float? by mutableStateOf(null)
    override val pieChartData = mutableStateMapOf<String, Float>()
    override var pieChartSum by mutableFloatStateOf(0f)
    override val operations: SnapshotStateList<OperationsUiModel> = mutableStateListOf()
    override val goals: SnapshotStateList<Pair<FinanceGoalEntity, Float>> = mutableStateListOf()

    private val financeOperationsRepository by inject<FinanceOperationsRepositoryI>()
    private val financeGoalRepository by inject<FinanceGoalsRepositoryI>()
    private val financeDataValidator by inject<FinanceDataValidatorI>()

    private suspend fun updatePieChartSum() {
        pieChartSum = financeGoalRepository.getAllGoals().sumOf {
            financeOperationsRepository.sumByGoalId(it.id).toDouble()
        }.toFloat()
    }

    private suspend fun updateTotalMoneyAmount() {
        totalMoney = financeOperationsRepository.getTotalMoneyAmount()
    }

    private suspend fun updateTotalGoalCost() {
        totalGoalCost = financeGoalRepository.getTotalCost()
    }

    private suspend fun addToGoalsList(newGoal: FinanceGoalEntity) {
        goals.add(newGoal to 0f)
        updateTotalGoalCost()
        pieChartData[newGoal.name] = 0f
    }

    private suspend fun removeFromGoalsList(removedGoalId: Int) {
        val index = goals.indexOfFirst { it.first.id == removedGoalId }
        val goalName = goals[index].first.name
        goals.removeAt(index)
        pieChartData.remove(goalName)
        updatePieChartSum()
    }

    private suspend fun loadGoalsList() {
        val transformedData = financeGoalRepository.getAllGoals().map {
            it to financeOperationsRepository.sumByGoalId(it.id)
        }
        goals.addAll(transformedData)
        pieChartData.putAll(
            transformedData.groupBy { it.first.name }.map {
                it.key to it.value.sumOf { (_, cost) ->
                    cost.toDouble()
                }.toFloat()
            }
        )
        updatePieChartSum()
    }

    private suspend fun addOperation(operation: FinanceOperationEntity) {
        operations.add(
            operation.let {
                val goal = financeGoalRepository.getGoalById(it.goalId)!!
                OperationsUiModel(
                    it.id,
                    it.amount,
                    it.goalId,
                    goal.name,
                    it.comment
                )
            }
        )
        val operationGoalId = operation.goalId
        val index = goals.indexOfFirst { it.first.id == operationGoalId }
        val newSum = financeOperationsRepository.sumByGoalId(operationGoalId)
        goals[index] = goals[index].copy(second = newSum)
        pieChartData[goals[index].first.name] = newSum
        updateTotalMoneyAmount()
        updatePieChartSum()
    }

    private suspend fun loadOperationsList() {
        operations.addAll(
            financeOperationsRepository.getAllOperations().map {
                val goal = it.goalId.let { goalId ->
                    financeGoalRepository.getGoalById(goalId)!!
                }
                OperationsUiModel(
                    it.id,
                    it.amount,
                    it.goalId,
                    goal.name,
                    it.comment
                )
            }
        )
    }

    private fun removeFromOperationsListByGoalId(removedGoalId: Int) {
        componentScope.launch {
            val goal = financeGoalRepository.getGoalById(removedGoalId)!!
            operations.removeIf { it.goalId == goal.id }
            pieChartData.remove(goal.name)
            updatePieChartSum()
        }
    }

    private suspend fun removeFromOperationsListById(removedOperationId: Int) {
        val index = operations.indexOfFirst { it.id == removedOperationId }
        val operation = operations[index]
        val goalId = operation.goalId
        val goalName = operation.goalName
        operations.removeAt(index)
        val goalIndex = goals.indexOfFirst { it.first.id == goalId }
        val newSum = financeOperationsRepository.sumByGoalId(goalId)
        goals[goalIndex] = goals[goalIndex].copy(second = newSum)
        pieChartData[goalName] = newSum
        updateTotalMoneyAmount()
        updatePieChartSum()
    }

    init {
        componentScope.launch {
            loadOperationsList()
            loadGoalsList()
            updateTotalMoneyAmount()
            updateTotalGoalCost()
        }
    }

    override fun onEvent(event: FinanceScreenEvent) {
        when (event) {
            is FinanceScreenEvent.AddGoal -> {
                componentScope.launch {
                    val goalEntity = FinanceGoalEntity(
                        amount = event.amount,
                        name = event.name
                    )
                    val goalId = financeGoalRepository.addGoal(goalEntity)
                    goalEntity.id = goalId
                    addToGoalsList(goalEntity)
                    // because now we already have such title
                    isGoalTitleValid = false
                }
            }

            is FinanceScreenEvent.AddOperation -> {
                componentScope.launch {
                    val entity = FinanceOperationEntity(
                        amount = event.amount,
                        goalId = event.goalId,
                        comment = event.comment
                    )
                    val insertedId = financeOperationsRepository.addOperation(entity)
                    entity.id = insertedId
                    addOperation(entity)
                }
            }

            FinanceScreenEvent.OnNavigationToMainScreen -> {
                onNavigationToMainScreen()
            }

            FinanceScreenEvent.OnNavigationToNewsFeedScreen -> {
                onNavigationToNewsFeedScreen()
            }

            is FinanceScreenEvent.OnGoalDeletion -> {
                componentScope.launch {
                    removeFromOperationsListByGoalId(event.goalId)
                    removeFromGoalsList(event.goalId)
                    financeGoalRepository.deleteGoal(event.goalId)
                    financeOperationsRepository.deleteByGoalId(event.goalId)
                    updatePieChartSum()
                    updateTotalMoneyAmount()
                    updateTotalGoalCost()
                }
            }

            is FinanceScreenEvent.OnOperationDeletion -> {
                componentScope.launch {
                    financeOperationsRepository.deleteOperation(event.operationId)
                    removeFromOperationsListById(event.operationId)
                }
            }

            is FinanceScreenEvent.GoalCostUpdate -> {
                val newCost = event.goalCost
                val (newGoalCost, newIsGoalCostValid) =
                    financeDataValidator.validateGoalCost(
                        goalCost,
                        newCost
                    )
                goalCost = newGoalCost
                isGoalCostValid = newIsGoalCostValid
            }

            is FinanceScreenEvent.GoalTitleUpdate -> {
                goalTitle = event.goalTitle
                isGoalTitleValid = financeDataValidator.validateGoalTitle(
                    event.goalTitle, goals.map { it.first.name }
                )
            }
        }
    }
}
