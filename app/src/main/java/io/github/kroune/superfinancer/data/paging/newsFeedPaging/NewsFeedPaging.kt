package io.github.kroune.superfinancer.data.paging.newsFeedPaging

import androidx.paging.PagingState
import io.github.kroune.superfinancer.data.sources.local.nyTimes.NewsCacheLocalDataSource
import io.github.kroune.superfinancer.data.sources.remote.nyTimes.newsFeedRemoteDataSource.NewFeedRemoteDataSourceI
import io.github.kroune.superfinancer.domain.models.ArticlesFeed
import io.github.kroune.superfinancer.domain.models.database.NewsCacheEntity
import io.github.kroune.superfinancer.domain.repositories.newsFeedRepository.NewFeedConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.coroutineContext

class NewsFeedPagingSource : KoinComponent, NewsFeedPagingI() {
    private val remoteDataSource by inject<NewFeedRemoteDataSourceI>()
    private val localDataSource by inject<NewsCacheLocalDataSource>()
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesFeed> {
        return try {
            val currentOffset = params.key ?: 0

            val data = run {
                val cache = localDataSource.getPaging(
                    currentOffset,
                    params.loadSize,
                    System.currentTimeMillis(),
                    NewFeedConstants.defaultTtl.inWholeMilliseconds
                )
                // if cache already exists
                if (cache.size == params.loadSize) {
                    cache
                } else {
                    val newData = remoteDataSource.getNewsFeed(currentOffset, params.loadSize)
                    // cache new data
                    newData.results.forEachIndexed { index, it ->
                        CoroutineScope(coroutineContext).launch {
                            localDataSource.updateCacheEntry(
                                NewsCacheEntity(
                                    newsFeedModel = it,
                                    offset = currentOffset + index,
                                    creationTime = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                    newData.results
                }
            }

            LoadResult.Page(
                data = data,
                prevKey = if (currentOffset == 0) null else currentOffset - 1,
                nextKey = if (data.size < params.loadSize) null else currentOffset + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ArticlesFeed>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun invalidateCache() {
        localDataSource.invalidateCache()
    }
}
