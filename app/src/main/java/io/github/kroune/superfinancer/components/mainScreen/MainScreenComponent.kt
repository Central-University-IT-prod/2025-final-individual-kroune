package io.github.kroune.superfinancer.components.mainScreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.superfinancer.componentCoroutineScope
import io.github.kroune.superfinancer.components.mainScreen.MainScreenConstants.tickerToLoadWhenSearching
import io.github.kroune.superfinancer.components.mainScreen.MainScreenConstants.timeBeforeRefreshingTickers
import io.github.kroune.superfinancer.data.json.DefaultJsonProviderI
import io.github.kroune.superfinancer.domain.models.ArticlesFeed
import io.github.kroune.superfinancer.domain.models.ArticlesSearchApiResponse
import io.github.kroune.superfinancer.domain.models.StockQuote
import io.github.kroune.superfinancer.domain.models.StockQuoteApiResponse
import io.github.kroune.superfinancer.domain.models.StockSearchApiResponse
import io.github.kroune.superfinancer.domain.repositories.articlesSearchRepository.ArticlesSearchRepositoryI
import io.github.kroune.superfinancer.domain.repositories.newsFeedRepository.NewsFeedRepositoryI
import io.github.kroune.superfinancer.domain.repositories.stockQuoteRepository.StockQuoteRepositoryI
import io.github.kroune.superfinancer.domain.repositories.stockSearchRepository.StockSearchRepositoryI
import io.github.kroune.superfinancer.events.MainScreenEvent
import io.github.kroune.superfinancer.ui.mainScreen.StockUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainScreenComponent(
    val onNavigationToArticleWebView: (url: String) -> Unit,
    val onNavigationToFinanceScreen: () -> Unit,
    val onNavigationToNewsFeedScreen: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, MainScreenComponentI, KoinComponent {
    private val newsFeedRepository by inject<NewsFeedRepositoryI>()

    override val newsFeed: Flow<PagingData<ArticlesFeed>> = newsFeedRepository
        .getNewsFeed()
        .flow
        .cachedIn(componentCoroutineScope())

    private val componentScope = componentCoroutineScope()
    private val stockQuoteRepository by inject<StockQuoteRepositoryI>()
    private val articlesSearchRepository by inject<ArticlesSearchRepositoryI>()
    private val stockSearchRepository by inject<StockSearchRepositoryI>()

    private val stocks by inject<DefaultJsonProviderI>()

    override val stockInfo: SnapshotStateList<StockQuoteApiResponse<StockUiModel>> =
        stocks.tickersList.map {
            StockQuoteApiResponse.Success(StockUiModel(it))
        }.toMutableStateList()
    private var tickersSearchJob: Job = Job()

    private var _articlesSearchResult: MutableState<ArticlesSearchApiResponse?> =
        mutableStateOf(null)
    override val articlesSearchResult: State<ArticlesSearchApiResponse?>
        get() = _articlesSearchResult
    private var articlesSearchJob: Job = Job()

    /**
     * basically we store a state of a stock search api response that contains a list of states of individual requests
     */
    private val _stocksSearchResult: MutableState<StockSearchApiResponse<List<StockQuoteApiResponse<StockUiModel>>>?> =
        mutableStateOf(null, referentialEqualityPolicy())
    override val stocksSearchResult: State<StockSearchApiResponse<List<StockQuoteApiResponse<StockUiModel>>>?> =
        _stocksSearchResult

    private fun refreshTickers() {
        componentCoroutineScope().launch {
            stocks.tickersList.forEachIndexed { index, it ->
                launch {
                    val stockQuote = quoteInfo(it)
                    stockInfo[index] = stockQuote
                }
            }
        }
    }

    init {
        componentScope.launch {
            while (true) {
                refreshTickers()
                delay(timeBeforeRefreshingTickers)
            }
        }
    }

    private suspend fun quoteInfo(stock: String): StockQuoteApiResponse<StockUiModel> {
        return stockQuoteRepository.getStockQuote(stock).let { response ->
            // it works
            @Suppress("UNCHECKED_CAST")
            if (response !is StockQuoteApiResponse.Success<StockQuote>)
                return@let response as StockQuoteApiResponse<StockUiModel>
            return@let with(response.data) {
                StockQuoteApiResponse.Success(
                    StockUiModel(
                        stock,
                        currentPrice,
                        change,
                        changePercent,
                        highestPriceOfTheDay,
                        lowestPriceOfTheDay,
                        openPriceOfTheDay,
                        previousClosePrice
                    )
                )
            }
        }
    }

    override fun onEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.ClickOnArticle -> {
                onNavigationToArticleWebView(event.article.url)
            }

            is MainScreenEvent.Search -> {
                tickersSearchJob.cancel()
                tickersSearchJob = componentScope.launch {
                    _stocksSearchResult.value = stockSearchRepository.search(event.query).let {
                        if (it !is StockSearchApiResponse.Success) {
                            // it works
                            @Suppress("UNCHECKED_CAST")
                            it as StockSearchApiResponse<List<StockQuoteApiResponse<StockUiModel>>>
                        } else {
                            StockSearchApiResponse.Success(
                                it.data.result.take(tickerToLoadWhenSearching).map {
                                    async {
                                        quoteInfo(it.displaySymbol)
                                    }
                                }.awaitAll()
                            )
                        }
                    }
                }
                articlesSearchJob.cancel()
                articlesSearchJob = componentScope.launch {
                    _articlesSearchResult.value =
                        articlesSearchRepository.searchForArticle(event.query)
                }
            }

            is MainScreenEvent.ClickOnSearchArticle -> {
                onNavigationToArticleWebView(event.article.webUrl)
            }

            MainScreenEvent.OnNavigationToFinanceScreen -> {
                onNavigationToFinanceScreen()
            }

            MainScreenEvent.OnNavigationToNewsFeedScreen -> {
                onNavigationToNewsFeedScreen()
            }

            MainScreenEvent.RefreshArticles -> {
                componentScope.launch {
                    newsFeedRepository.invalidateCache()
                    newsFeedRepository.refreshNewsFeed()
                    refreshTickers()
                }
            }
        }
    }
}
