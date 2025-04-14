package io.github.kroune.superfinancer.domain.repositories.newsFeedRepository

import androidx.paging.Pager
import io.github.kroune.superfinancer.domain.models.ArticlesFeed

interface NewsFeedRepositoryI {
    fun getNewsFeed(): Pager<Int, ArticlesFeed>
    fun refreshNewsFeed()
    suspend fun invalidateCache()
}
