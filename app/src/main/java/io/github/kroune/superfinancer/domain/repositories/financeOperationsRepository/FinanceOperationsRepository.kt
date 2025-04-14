package io.github.kroune.superfinancer.domain.repositories.financeOperationsRepository

import io.github.kroune.superfinancer.data.sources.local.finance.FinanceOperationsLocalDataSource
import io.github.kroune.superfinancer.domain.models.database.FinanceOperationEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FinanceOperationsRepository : FinanceOperationsRepositoryI, KoinComponent {
    private val financeDAO by inject<FinanceOperationsLocalDataSource>()

    override suspend fun addOperation(info: FinanceOperationEntity): Int {
        return financeDAO.insert(info).toInt()
    }

    override suspend fun getAllOperations(): List<FinanceOperationEntity> {
        return financeDAO.getAll()
    }

    /**
     * @return sum of all operations
     */
    override suspend fun getTotalMoneyAmount(): Float {
        return financeDAO.getTotalMonetAmount()
    }

    override suspend fun sumByGoalId(operationId: Int): Float {
        return financeDAO.sumByGoalId(operationId)
    }

    override suspend fun deleteOperation(operationId: Int) {
        return financeDAO.deleteByOperationId(operationId)
    }

    override suspend fun deleteByGoalId(goalId: Int) {
        financeDAO.deleteByGoalId(goalId)
    }
}
