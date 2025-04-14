package io.github.kroune.superfinancer.data.sources.local.nyTimes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.kroune.superfinancer.domain.models.ArticlesFeed
import io.github.kroune.superfinancer.domain.models.database.NewsCacheEntity
import io.github.kroune.superfinancer.domain.repositories.newsFeedRepository.NewFeedConstants

@Dao
interface NewsCacheLocalDataSource {
    suspend fun getPaging(
        offsetValue: Int,
        amount: Int,
        currentTime: Long,
        ttl: Long
    ): List<ArticlesFeed> {
        val result = mutableListOf<ArticlesFeed>()
        for (i in offsetValue..<offsetValue + amount) {
            val entry = getLatestEntry(i, currentTime, ttl) ?: return emptyList()
            result.add(entry)
        }
        return result
    }

    @Query(
        """
            SELECT news_feed_model FROM NewsCacheEntity 
            WHERE (offset_value == :offset AND :currentTime - creation_time <= :ttl) 
            ORDER BY creation_time DESC
        """
    )
    suspend fun getLatestEntry(
        offset: Int,
        currentTime: Long,
        ttl: Long
    ): ArticlesFeed?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cache: NewsCacheEntity): Long

    @Transaction
    suspend fun updateCacheEntry(cache: NewsCacheEntity): Long {
        plannedCacheInvalidation(
            System.currentTimeMillis(),
            NewFeedConstants.defaultTtl.inWholeMilliseconds
        )
        return insert(cache)
    }

    @Query("DELETE FROM NewsCacheEntity WHERE 1")
    suspend fun invalidateCache()

    @Query("DELETE FROM NewsCacheEntity WHERE (:currentTime - creation_time > :ttl)")
    suspend fun plannedCacheInvalidation(currentTime: Long, ttl: Long)
}
