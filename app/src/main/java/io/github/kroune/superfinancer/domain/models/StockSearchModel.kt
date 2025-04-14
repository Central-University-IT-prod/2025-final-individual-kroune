package io.github.kroune.superfinancer.domain.models

import kotlinx.serialization.Serializable

sealed interface StockSearchApiResponse<T> {
    data class Success<T>(val data: T) : StockSearchApiResponse<T>
    class TooManyRequests<T> : StockSearchApiResponse<T>
    class ServerError<T> : StockSearchApiResponse<T>
    class NetworkError<T> : StockSearchApiResponse<T>
    class UnknownError<T> : StockSearchApiResponse<T>
}

@Serializable
data class StockSearch(
    val count: Int,
    val result: List<SearchResult>
)

@Serializable
data class SearchResult(
    val description: String,
    val displaySymbol: String,
    val symbol: String,
    val type: String
)
