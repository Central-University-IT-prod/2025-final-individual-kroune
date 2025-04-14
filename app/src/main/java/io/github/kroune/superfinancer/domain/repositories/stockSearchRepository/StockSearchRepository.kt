package io.github.kroune.superfinancer.domain.repositories.stockSearchRepository

import io.github.kroune.superfinancer.data.sources.remote.finHub.stockSearchRemoteDataSource.StockSearchRemoteDataSourceI
import io.github.kroune.superfinancer.domain.models.StockSearch
import io.github.kroune.superfinancer.domain.models.StockSearchApiResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StockSearchRepository : StockSearchRepositoryI, KoinComponent {
    private val remote by inject<StockSearchRemoteDataSourceI>()

    override suspend fun search(query: String): StockSearchApiResponse<StockSearch> {
        return remote.search(query)
    }
}
