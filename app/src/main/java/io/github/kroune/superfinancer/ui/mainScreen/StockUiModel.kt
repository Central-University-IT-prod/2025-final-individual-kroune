package io.github.kroune.superfinancer.ui.mainScreen

data class StockUiModel(
    val name: String,
    val currentPrice: Double? = null,
    val change: Double? = null,
    val changePercent: Double? = null,
    val highestPrice: Double? = null,
    val lowestPrice: Double? = null,
    val openingPrice: Double? = null,
    val previousClosePrice: Double? = null,
)
