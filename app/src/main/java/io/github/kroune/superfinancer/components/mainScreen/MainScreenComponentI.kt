package io.github.kroune.superfinancer.components.mainScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.paging.PagingData
import io.github.kroune.superfinancer.domain.models.ArticlesFeed
import io.github.kroune.superfinancer.domain.models.ArticlesSearchApiResponse
import io.github.kroune.superfinancer.domain.models.StockQuoteApiResponse
import io.github.kroune.superfinancer.domain.models.StockSearchApiResponse
import io.github.kroune.superfinancer.events.MainScreenEvent
import io.github.kroune.superfinancer.ui.mainScreen.StockUiModel
import kotlinx.coroutines.flow.Flow

interface MainScreenComponentI {
    val newsFeed: Flow<PagingData<ArticlesFeed>>
    val stockInfo: SnapshotStateList<StockQuoteApiResponse<StockUiModel>>
    val articlesSearchResult: State<ArticlesSearchApiResponse?>
    fun onEvent(event: MainScreenEvent)
    val stocksSearchResult: State<StockSearchApiResponse<List<StockQuoteApiResponse<StockUiModel>>>?>
}
