package io.github.kroune.superfinancer.domain.models.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = FinanceGoalEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("goal_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class FinanceOperationEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "amount")
    val amount: Float,
    @ColumnInfo(name = "goal_id")
    val goalId: Int,
    @ColumnInfo(name = "comment")
    val comment: String?
)
