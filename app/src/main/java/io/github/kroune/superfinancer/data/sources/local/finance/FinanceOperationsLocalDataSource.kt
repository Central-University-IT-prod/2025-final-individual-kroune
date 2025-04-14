package io.github.kroune.superfinancer.data.sources.local.finance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.kroune.superfinancer.domain.models.database.FinanceOperationEntity

@Dao
interface FinanceOperationsLocalDataSource {
    @Query("SELECT * FROM FinanceOperationEntity ORDER BY id")
    suspend fun getAll(): List<FinanceOperationEntity>

    @Query("SELECT SUM(amount) as TotalSum FROM FinanceOperationEntity")
    suspend fun getTotalMonetAmount(): Float

    @Query("SELECT SUM(amount) FROM FinanceOperationEntity WHERE goal_id IS (:goalId)")
    suspend fun sumByGoalId(goalId: Int): Float

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FinanceOperationEntity): Long

    @Query("DELETE FROM FinanceOperationEntity WHERE (id = :operationId)")
    suspend fun deleteByOperationId(operationId: Int)

    @Query("DELETE FROM FinanceOperationEntity WHERE (goal_id == :goalId)")
    suspend fun deleteByGoalId(goalId: Int)
}
