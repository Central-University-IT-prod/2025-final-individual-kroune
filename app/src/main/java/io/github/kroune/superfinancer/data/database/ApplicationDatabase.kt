package io.github.kroune.superfinancer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import io.github.kroune.superfinancer.data.sources.local.finance.FinanceGoalsLocalDataSource
import io.github.kroune.superfinancer.data.sources.local.finance.FinanceOperationsLocalDataSource
import io.github.kroune.superfinancer.data.sources.local.nyTimes.NewsCacheLocalDataSource
import io.github.kroune.superfinancer.domain.models.ArticlesFeed
import io.github.kroune.superfinancer.domain.models.database.FinanceGoalEntity
import io.github.kroune.superfinancer.domain.models.database.FinanceOperationEntity
import io.github.kroune.superfinancer.domain.models.database.NewsCacheEntity
import kotlinx.serialization.json.Json
import java.time.Instant

@TypeConverters(DateTimeConvertor::class, ArticlesFeedConvertor::class)
@Database(
    entities = [FinanceOperationEntity::class, FinanceGoalEntity::class, NewsCacheEntity::class],
    version = 1
)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun financeOperationsDao(): FinanceOperationsLocalDataSource
    abstract fun newsCacheOperationsDao(): NewsCacheLocalDataSource
    abstract fun financeGoalDao(): FinanceGoalsLocalDataSource
}

class DateTimeConvertor {
    @TypeConverter
    fun convertToDatabaseColumn(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun convertToEntityAttribute(date: Long?): Instant? {
        return date?.let { Instant.ofEpochMilli(it) }
    }
}

class ArticlesFeedConvertor {
    @TypeConverter
    fun convertToDatabaseColumn(articlesFeed: ArticlesFeed?): String? {
        return articlesFeed?.let {
            Json.encodeToString(articlesFeed)
        }
    }

    @TypeConverter
    fun convertToEntityAttribute(articlesFeed: String?): ArticlesFeed? {
        return articlesFeed?.let {
            Json.decodeFromString<ArticlesFeed>(it)
        }
    }
}
