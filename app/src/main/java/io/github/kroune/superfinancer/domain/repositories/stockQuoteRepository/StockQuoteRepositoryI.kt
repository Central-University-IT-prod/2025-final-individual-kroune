package io.github.kroune.superfinancer.domain.repositories.stockQuoteRepository

import io.github.kroune.superfinancer.domain.models.StockQuote
import io.github.kroune.superfinancer.domain.models.StockQuoteApiResponse

interface StockQuoteRepositoryI {
    suspend fun getStockQuote(stock: String): StockQuoteApiResponse<StockQuote>
}
