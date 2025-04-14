package io.github.kroune.superfinancer.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface StockQuoteApiResponse<T> {
    data class Success<T>(val data: T) : StockQuoteApiResponse<T>
    class TooManyRequests<T> : StockQuoteApiResponse<T>
    class ServerError<T> : StockQuoteApiResponse<T>
    class NetworkError<T> : StockQuoteApiResponse<T>
    class UnknownError<T> : StockQuoteApiResponse<T>
}

@Serializable
data class StockQuote(
    @SerialName("c")
    val currentPrice: Double,
    @SerialName("d")
    val change: Double?,
    @SerialName("dp")
    val changePercent: Double?,
    @SerialName("h")
    val highestPriceOfTheDay: Double,
    @SerialName("l")
    val lowestPriceOfTheDay: Double,
    @SerialName("o")
    val openPriceOfTheDay: Double,
    @SerialName("pc")
    val previousClosePrice: Double,
)
