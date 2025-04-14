package io.github.kroune.superfinancer.data.paging.newsFeedPaging

import androidx.paging.PagingSource
import io.github.kroune.superfinancer.domain.models.ArticlesFeed

abstract class NewsFeedPagingI: PagingSource<Int, ArticlesFeed>() {
    abstract suspend fun invalidateCache()
}
