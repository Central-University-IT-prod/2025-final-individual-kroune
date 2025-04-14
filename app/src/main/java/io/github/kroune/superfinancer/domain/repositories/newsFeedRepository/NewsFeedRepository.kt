package io.github.kroune.superfinancer.domain.repositories.newsFeedRepository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import io.github.kroune.superfinancer.data.paging.newsFeedPaging.NewsFeedPagingI
import io.github.kroune.superfinancer.domain.models.ArticlesFeed
import org.intellij.lang.annotations.MagicConstant
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.time.Duration.Companion.minutes

class NewsFeedRepository : NewsFeedRepositoryI, KoinComponent {
    private lateinit var pagingSource: NewsFeedPagingI
    override fun getNewsFeed(): Pager<Int, ArticlesFeed> {
        return Pager(
            config = PagingConfig(10, 10),
            pagingSourceFactory = {
                pagingSource = get<NewsFeedPagingI>()
                pagingSource
            }
        )
    }

    /**
     * This function should only be called after [pagingSource] has been initialized
     */
    override fun refreshNewsFeed() {
        if (::pagingSource.isInitialized)
            pagingSource.invalidate()
    }

    /**
     * This function should only be called after [pagingSource] has been initialized
     */
    override suspend fun invalidateCache() {
        if (::pagingSource.isInitialized)
            pagingSource.invalidateCache()
    }
}

object NewFeedConstants {
    @MagicConstant
    val defaultTtl = 20.minutes
}
