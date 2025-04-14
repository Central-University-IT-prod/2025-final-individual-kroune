package io.github.kroune.superfinancer.domain.repositories.financerGoalsRepository

import io.github.kroune.superfinancer.data.sources.local.finance.FinanceGoalsLocalDataSource
import io.github.kroune.superfinancer.domain.models.database.FinanceGoalEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FinanceGoalsRepository : FinanceGoalsRepositoryI, KoinComponent {
    private val financeDAO by inject<FinanceGoalsLocalDataSource>()

    override suspend fun getGoalById(id: Int): FinanceGoalEntity? {
        return financeDAO.goalById(id)
    }

    override suspend fun addGoal(info: FinanceGoalEntity): Int {
        return financeDAO.insert(info).toInt()
    }

    override suspend fun getAllGoals(): List<FinanceGoalEntity> {
        return financeDAO.getAll()
    }

    override suspend fun getTotalCost(): Float {
        return financeDAO.getTotalAmount()
    }

    override suspend fun deleteGoal(id: Int) {
        financeDAO.deleteById(id)
    }
}
