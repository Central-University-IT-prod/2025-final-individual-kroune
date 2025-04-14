package io.github.kroune.superfinancer.domain.models.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.kroune.superfinancer.domain.models.ArticlesFeed

@Entity
data class NewsCacheEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", index = true)
    var id: Int = 0,
    @ColumnInfo(name = "offset_value", index = true)
    var offset: Int,
    @ColumnInfo(name = "news_feed_model")
    val newsFeedModel: ArticlesFeed,
    @ColumnInfo(name = "creation_time", index = true)
    val creationTime: Long,
)
