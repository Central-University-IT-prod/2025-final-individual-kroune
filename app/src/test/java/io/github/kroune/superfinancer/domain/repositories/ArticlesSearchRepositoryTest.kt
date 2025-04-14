package io.github.kroune.superfinancer.domain.repositories

import io.github.kroune.superfinancer.data.sources.remote.nyTimes.articlesSearchRemoteDataSource.ArticlesSearchRemoteDataSourceI
import io.github.kroune.superfinancer.domain.models.ArticlesSearchApiResponse
import io.github.kroune.superfinancer.domain.models.ArticlesSearchModel
import io.github.kroune.superfinancer.domain.models.Response
import io.github.kroune.superfinancer.domain.repositories.articlesSearchRepository.ArticlesSearchRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.assertTrue

class ArticlesSearchRepositoryTest : KoinTest {
    val articlesResponse = ArticlesSearchApiResponse.Success(
        ArticlesSearchModel(
            "status", "copyright", Response(
                listOf()
            )
        )
    )
    private val remote: ArticlesSearchRemoteDataSourceI = object : ArticlesSearchRemoteDataSourceI {
        override suspend fun searchForArticle(query: String): ArticlesSearchApiResponse {
            return articlesResponse
        }

    }

    @Test
    fun `should return proxy requests`() {
        val repository = ArticlesSearchRepository()
        startKoin {
            loadKoinModules(
                module {
                    single<ArticlesSearchRemoteDataSourceI> { remote }
                }
            )
        }
        val result = runBlocking { repository.searchForArticle("someRandomQuery") }

        assertTrue { articlesResponse === result }
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}
