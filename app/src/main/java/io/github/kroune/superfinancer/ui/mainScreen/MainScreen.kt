package io.github.kroune.superfinancer.ui.mainScreen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.kroune.super_financer_ui.theme.SuperFinancer
import io.github.kroune.superfinancer.R
import io.github.kroune.superfinancer.components.mainScreen.MainScreenComponentI
import io.github.kroune.superfinancer.domain.models.ArticlesSearchApiResponse
import io.github.kroune.superfinancer.domain.models.StockQuoteApiResponse
import io.github.kroune.superfinancer.domain.models.StockSearchApiResponse
import io.github.kroune.superfinancer.events.MainScreenEvent
import io.github.kroune.superfinancer.mapper.toArticleUiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    component: MainScreenComponentI
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    with(component.articlesSearchResult.value) {
        val resourceId = when (this@with) {
            is ArticlesSearchApiResponse.Success, null -> return@with
            ArticlesSearchApiResponse.TooManyRequests -> R.string.articles_too_many_requests
            ArticlesSearchApiResponse.Unauthorized -> R.string.articles_unauthorized
            ArticlesSearchApiResponse.UnknownError -> R.string.articles_unknown_error
            ArticlesSearchApiResponse.NetworkError -> R.string.articles_network_error
        }
        val message = stringResource(resourceId)
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { contentPadding ->
        Column(
            Modifier.padding(contentPadding)
        ) {
            Text(
                stringResource(R.string.main_title),
                modifier = Modifier
                    .padding(horizontal = SuperFinancer.screenSpacing.defaultHorizontalSpacing),
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
            var searchText by rememberSaveable { mutableStateOf("") }
            SearchBar(
                {
                    TextField(
                        searchText,
                        {
                            searchText = it
                            component.onEvent(MainScreenEvent.Search(it))
                        },
                        placeholder = {
                            Text(stringResource(R.string.search))
                        },
                        trailingIcon = {
                            if (searchText != "") {
                                IconButton({
                                    searchText = ""
                                }) {
                                    Icon(painterResource(R.drawable.close), "clear text")
                                }
                            }
                        }
                    )
                },
                searchText != "",
                {
                },
                modifier = Modifier
                    .padding(horizontal = SuperFinancer.screenSpacing.defaultHorizontalSpacing)
                    .clip(RoundedCornerShape(10))
                    .fillMaxWidth()
            ) {
                if (searchText != "") {
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = SuperFinancer.screenSpacing.defaultHorizontalSpacing)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val stockInfo = component.stocksSearchResult.value
                        if (stockInfo is StockSearchApiResponse.Success) {
                            val stockInfoResult = stockInfo.data
                            items(stockInfoResult.size, key = { "ticker-$it" }) {
                                val element = stockInfoResult[it]
                                if (element is StockQuoteApiResponse.Success)
                                    DrawTicker(
                                        element.data,
                                        Modifier.fillMaxWidth()
                                    )
                            }
                        }
                        val searchResult = component.articlesSearchResult.value
                        if (searchResult == null || searchResult !is ArticlesSearchApiResponse.Success)
                            return@LazyColumn
                        val articles = searchResult.data.response.docs
                        items(articles.size, key = { "article-$it" }) {
                            DrawArticle(
                                articles[it].toArticleUiModel()
                            ) {
                                component.onEvent(MainScreenEvent.ClickOnSearchArticle(articles[it]))
                            }
                        }
                    }
                }
            }
            val newsFeedItems = component.newsFeed.collectAsLazyPagingItems()
            var isRefreshing by remember { mutableStateOf(false) }

            val displaySkeleton = remember { MutableTransitionState(true) }
            var skeletonRendered by remember { mutableStateOf(false) }
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    scope.launch {
                        component.onEvent(MainScreenEvent.RefreshArticles)
                        // we simply tell pager to refresh, there is nothing to wait, so we simply add some nice delay
                        delay(600)
                        isRefreshing = false
                    }
                },
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (displaySkeleton.currentState) {
                        item {
                            Spacer(Modifier.height(10.dp))
                        }
                        items(10, { "skeletion-$it" }) {
                            skeletonRendered = true
                            // it has to be this way, or we will have to use an implicit receiver
                            androidx.compose.animation.AnimatedVisibility(
                                displaySkeleton,
                            ) {
                                DrawArticleSkeleton()
                            }
                        }
                    }
                    item {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // initial offset
                            item {
                                Spacer(Modifier.width(3.dp))
                            }
                            items(component.stockInfo.size, key = { it }) {
                                val element = component.stockInfo[it]
                                if (element is StockQuoteApiResponse.Success) {
                                    DrawTicker(element.data)
                                } else {
                                    // idea is smart enough to understand that it is unreachable
                                    // but kotlin compiler isn't
                                    @Suppress("KotlinConstantConditions")
                                    val string = when (element) {
                                        is StockQuoteApiResponse.NetworkError -> {
                                            stringResource(R.string.ticker_network_error)
                                        }

                                        is StockQuoteApiResponse.ServerError -> {
                                            stringResource(R.string.ticker_server_error)
                                        }

                                        is StockQuoteApiResponse.Success -> error("")
                                        is StockQuoteApiResponse.TooManyRequests -> {
                                            stringResource(R.string.ticker_too_many_requests)
                                        }

                                        is StockQuoteApiResponse.UnknownError -> {
                                            stringResource(R.string.ticker_unknown_error)
                                        }
                                    }
                                    LaunchedEffect(element) {
                                        snackbarHostState.showSnackbar(string)
                                    }
                                }
                            }
                            // offset at the end
                            item {
                                Spacer(Modifier.width(3.dp))
                            }
                        }
                    }
                    items(newsFeedItems.itemCount, key = { it }) { index ->
                        val article = newsFeedItems[index]!!
                        DrawArticle(
                            article.toArticleUiModel()
                        ) { component.onEvent(MainScreenEvent.ClickOnArticle(article)) }
                    }
                    if (newsFeedItems.loadState.append == LoadState.Loading) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
            // if we update visibility state before skeleton was rendered it wouldn't hide properly
            if (skeletonRendered) {
                val value =
                    (newsFeedItems.loadState.append is LoadState.NotLoading && newsFeedItems.itemCount == 0)
                if (!value) displaySkeleton.targetState = false
            }
        }
    }
}

@Composable
fun DrawArticleSkeleton() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val skeletonAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    Card(
        modifier = Modifier
            .alpha(skeletonAlpha)
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
    ) {
    }
}

@Composable
fun DrawTicker(
    stockUiModel: StockUiModel,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        shape = RoundedCornerShape(15)
    ) {
        Row(
            modifier = modifier
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stockUiModel.name
            )
            Spacer(modifier = Modifier.width(20.dp))
            val currentPrice = stockUiModel.currentPrice
            val changePercentage = stockUiModel.changePercent
            if (currentPrice != null && changePercentage != null) {
                Text(
                    "${"%.2f".format(currentPrice)} (${"%.1f".format(changePercentage)}%)" + when (changePercentage) {
                        in (Double.NEGATIVE_INFINITY..0.0) -> {
                            "▼"
                        }

                        in (0.0..Double.POSITIVE_INFINITY) -> {
                            "▲"
                        }

                        else -> {
                            " "
                        }
                    },
                    color = when (changePercentage) {
                        in (Double.NEGATIVE_INFINITY..0.0) -> {
                            SuperFinancer.fixedAccentColors.lossColor
                        }

                        in (0.0..Double.POSITIVE_INFINITY) -> {
                            SuperFinancer.fixedAccentColors.gainColor
                        }

                        else -> {
                            Color.Black
                        }
                    }
                )
            } else {
                Spacer(Modifier.width(15.dp))
            }
        }
    }
}

@Composable
fun DrawArticle(
    article: ArticleUiModel,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.padding(horizontal = SuperFinancer.screenSpacing.defaultHorizontalSpacing),
        onClick = {
            onClick()
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(5)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .requiredHeightIn(min = 20.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(article.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(5))
                    .border(1.dp, Color.Black, RoundedCornerShape(5)),
                contentScale = ContentScale.FillWidth
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                article.title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(article.abstract)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    article.source ?: stringResource(R.string.unknown_source),
                    fontSize = 10.sp
                )
                Text(
                    article.publicationDate,
                    fontSize = 10.sp
                )
            }
        }
    }
}
