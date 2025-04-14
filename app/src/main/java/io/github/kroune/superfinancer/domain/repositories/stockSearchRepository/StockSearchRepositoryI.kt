package io.github.kroune.superfinancer.domain.repositories.stockSearchRepository

import io.github.kroune.superfinancer.domain.models.StockSearch
import io.github.kroune.superfinancer.domain.models.StockSearchApiResponse

interface StockSearchRepositoryI {
    suspend fun search(query: String): StockSearchApiResponse<StockSearch>
}
