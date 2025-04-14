package io.github.kroune.superfinancer.domain.repositories.articlesSearchRepository

import io.github.kroune.superfinancer.domain.models.ArticlesSearchApiResponse

interface ArticlesSearchRepositoryI {
    suspend fun searchForArticle(query: String): ArticlesSearchApiResponse
}
