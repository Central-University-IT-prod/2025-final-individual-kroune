package io.github.kroune.superfinancer.domain.repositories.financerGoalsRepository

import io.github.kroune.superfinancer.domain.models.database.FinanceGoalEntity

interface FinanceGoalsRepositoryI {
    suspend fun getGoalById(id: Int): FinanceGoalEntity?
    suspend fun addGoal(info: FinanceGoalEntity): Int
    suspend fun getAllGoals(): List<FinanceGoalEntity>
    suspend fun getTotalCost(): Float
    suspend fun deleteGoal(id: Int)
}
