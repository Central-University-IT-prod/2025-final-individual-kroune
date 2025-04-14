package io.github.kroune.superfinancer.events

import io.github.kroune.superfinancer.domain.models.ArticlesFeed
import io.github.kroune.superfinancer.domain.models.SearchArticle

sealed interface MainScreenEvent {
    data class ClickOnArticle(val article: ArticlesFeed): MainScreenEvent
    data class ClickOnSearchArticle(val article: SearchArticle): MainScreenEvent
    data object RefreshArticles: MainScreenEvent
    data class Search(val query: String): MainScreenEvent
    data object OnNavigationToFinanceScreen: MainScreenEvent
    data object OnNavigationToNewsFeedScreen: MainScreenEvent
}
