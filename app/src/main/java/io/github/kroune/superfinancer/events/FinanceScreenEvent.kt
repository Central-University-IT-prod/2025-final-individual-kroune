package io.github.kroune.superfinancer.events

sealed class FinanceScreenEvent {
    data class AddOperation(
        val amount: Float,
        val goalId: Int,
        val comment: String?
    ) : FinanceScreenEvent()

    data class AddGoal(val name: String, val amount: Float) : FinanceScreenEvent()
    data object OnNavigationToMainScreen : FinanceScreenEvent()
    data object OnNavigationToNewsFeedScreen : FinanceScreenEvent()
    data class OnGoalDeletion(val goalId: Int): FinanceScreenEvent()
    data class OnOperationDeletion(val operationId: Int): FinanceScreenEvent()
    data class GoalTitleUpdate(val goalTitle: String): FinanceScreenEvent()
    data class GoalCostUpdate(val goalCost: String): FinanceScreenEvent()
}
