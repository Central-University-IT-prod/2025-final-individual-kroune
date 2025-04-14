package io.github.kroune.superfinancer.domain.usecases

interface FinanceDataValidatorI {
    fun validateGoalTitle(title: String, otherGoals: List<String>): Boolean
    fun validateGoalCost(oldCost: String, newCost: String): Pair<String, Boolean>
}
