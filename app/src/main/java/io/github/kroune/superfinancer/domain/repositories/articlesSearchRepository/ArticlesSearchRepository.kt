package io.github.kroune.superfinancer.domain.repositories.articlesSearchRepository

import io.github.kroune.superfinancer.data.sources.remote.nyTimes.articlesSearchRemoteDataSource.ArticlesSearchRemoteDataSourceI
import io.github.kroune.superfinancer.domain.models.ArticlesSearchApiResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ArticlesSearchRepository : ArticlesSearchRepositoryI, KoinComponent {
    private val remote by inject<ArticlesSearchRemoteDataSourceI>()

    override suspend fun searchForArticle(query: String): ArticlesSearchApiResponse {
        return remote.searchForArticle(query)
    }
}
