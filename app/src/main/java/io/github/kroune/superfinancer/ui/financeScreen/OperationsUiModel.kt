package io.github.kroune.superfinancer.ui.financeScreen

data class OperationsUiModel(
    val id: Int,
    val amount: Float,
    val goalId: Int,
    val goalName: String,
    val comment: String?
)
