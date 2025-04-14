package io.github.kroune.superfinancer.data.sources.remote.nyTimes.articlesSearchRemoteDataSource

import io.github.kroune.superfinancer.domain.models.ArticlesSearchApiResponse

interface ArticlesSearchRemoteDataSourceI {
    suspend fun searchForArticle(query: String): ArticlesSearchApiResponse
}
