package io.github.kroune.superfinancer.domain.repositories

import androidx.paging.PagingState
import io.github.kroune.superfinancer.data.paging.newsFeedPaging.NewsFeedPagingI
import io.github.kroune.superfinancer.domain.models.ArticlesFeed
import io.github.kroune.superfinancer.domain.repositories.newsFeedRepository.NewsFeedRepository
import io.github.kroune.superfinancer.domain.repositories.newsFeedRepository.NewsFeedRepositoryI
import io.github.kroune.superfinancer.noNeed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject


class FakeNewsFeedPagingSource : NewsFeedPagingI() {
    override suspend fun invalidateCache() {
        noNeed()
    }

    override fun getRefreshKey(state: PagingState<Int, ArticlesFeed>): Int {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesFeed> {
        return LoadResult.Page(emptyList(), null, null)
    }
}

class NewsFeedRepositoryTest : KoinTest {

    private val testModule = module {
        single<NewsFeedPagingI> { FakeNewsFeedPagingSource() }
        single<NewsFeedRepositoryI> { NewsFeedRepository() }
    }
    private val repository: NewsFeedRepositoryI by inject()

    @Before
    fun setUp() {
        startKoin {
            modules(testModule)
        }
    }

    @Test
    fun testRefreshNewsFeed() {
        runBlocking {
            repository.getNewsFeed() // Initialize pagingSource
            repository.refreshNewsFeed()
            // No exception means success
        }
    }

    @Test
    fun testRefreshNewsFeed2() {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                repository.getNewsFeed().flow // Initialize pagingSource
            }
            repository.refreshNewsFeed()
            // No exception means success
        }
    }

    @Test
    fun testRefreshNewsFeedAndGetNewsAgain() {
        runBlocking {
            repository.getNewsFeed()
            repository.invalidateCache()
            repository.getNewsFeed()
            // No exception means success
        }
    }

    @Test
    fun testBadOrder() {
        runBlocking {
            repository.invalidateCache()
            // No exception means success
        }
    }

    @Test
    fun testBadOrder2() {
        runBlocking {
            repository.refreshNewsFeed()
            // No exception means success
        }
    }

    @Test
    fun testInvalidateCache() {
        runBlocking {
            repository.getNewsFeed() // Initialize pagingSource
            repository.invalidateCache()
            // No exception means success
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}
