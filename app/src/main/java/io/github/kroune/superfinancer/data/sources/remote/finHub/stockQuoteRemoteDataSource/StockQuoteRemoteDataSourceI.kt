package io.github.kroune.superfinancer.data.sources.remote.finHub.stockQuoteRemoteDataSource

import io.github.kroune.superfinancer.domain.models.StockQuote
import io.github.kroune.superfinancer.domain.models.StockQuoteApiResponse

interface StockQuoteRemoteDataSourceI {
    suspend fun getQuote(stock: String): StockQuoteApiResponse<StockQuote>
}
