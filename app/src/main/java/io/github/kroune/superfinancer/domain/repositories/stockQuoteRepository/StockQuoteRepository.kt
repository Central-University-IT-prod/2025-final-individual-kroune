package io.github.kroune.superfinancer.domain.repositories.stockQuoteRepository

import io.github.kroune.superfinancer.data.sources.remote.finHub.stockQuoteRemoteDataSource.StockQuoteRemoteDataSourceI
import io.github.kroune.superfinancer.domain.models.StockQuote
import io.github.kroune.superfinancer.domain.models.StockQuoteApiResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StockQuoteRepository : KoinComponent, StockQuoteRepositoryI {
    private val remote by inject<StockQuoteRemoteDataSourceI>()
    override suspend fun getStockQuote(stock: String): StockQuoteApiResponse<StockQuote> {
        return remote.getQuote(stock)
    }
}
