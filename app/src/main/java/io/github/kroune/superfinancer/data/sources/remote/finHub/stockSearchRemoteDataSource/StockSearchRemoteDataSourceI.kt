package io.github.kroune.superfinancer.data.sources.remote.finHub.stockSearchRemoteDataSource

import io.github.kroune.superfinancer.domain.models.StockSearch
import io.github.kroune.superfinancer.domain.models.StockSearchApiResponse

interface StockSearchRemoteDataSourceI {
    suspend fun search(query: String): StockSearchApiResponse<StockSearch>
}
