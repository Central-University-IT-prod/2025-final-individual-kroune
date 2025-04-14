package io.github.kroune.superfinancer.data.sources.local.finance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.kroune.superfinancer.domain.models.database.FinanceGoalEntity

@Dao
interface FinanceGoalsLocalDataSource {
    @Query("SELECT * FROM FinanceGoalEntity")
    suspend fun getAll(): List<FinanceGoalEntity>

    @Query("SELECT SUM(amount) as TotalSum FROM FinanceGoalEntity")
    suspend fun getTotalAmount(): Float

    @Query("SELECT * FROM FinanceGoalEntity WHERE id = :goalId LIMIT 1")
    suspend fun goalById(goalId: Int): FinanceGoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: FinanceGoalEntity): Long

    @Query("DELETE FROM FinanceGoalEntity WHERE id = :goalId")
    suspend fun deleteById(goalId: Int)
}
