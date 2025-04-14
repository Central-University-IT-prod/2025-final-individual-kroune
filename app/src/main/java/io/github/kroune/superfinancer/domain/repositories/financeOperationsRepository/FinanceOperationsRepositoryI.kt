package io.github.kroune.superfinancer.domain.repositories.financeOperationsRepository

import io.github.kroune.superfinancer.domain.models.database.FinanceOperationEntity

interface FinanceOperationsRepositoryI {
    suspend fun addOperation(info: FinanceOperationEntity): Int
    suspend fun getAllOperations(): List<FinanceOperationEntity>
    suspend fun getTotalMoneyAmount(): Float
    suspend fun sumByGoalId(operationId: Int): Float
    suspend fun deleteOperation(operationId: Int)
    suspend fun deleteByGoalId(goalId: Int)
}
