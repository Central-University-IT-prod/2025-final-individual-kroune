package io.github.kroune.superfinancer.mapper

import io.github.kroune.superfinancer.domain.models.ArticlesFeed
import io.github.kroune.superfinancer.domain.models.SearchArticle
import io.github.kroune.superfinancer.ui.mainScreen.ArticleUiModel

fun SearchArticle.toArticleUiModel(): ArticleUiModel {
    return ArticleUiModel(
        title = headline.main,
        abstract = abstract,
        source = source,
        url = webUrl,
        publicationDate = publishDate,
        imageUrl = "https://static01.nyt.com/" + multimedia.maxByOrNull { media ->
            media.width.toDouble() / media.height
        }?.url
    )
}

fun ArticlesFeed.toArticleUiModel(): ArticleUiModel {
    return ArticleUiModel(
        title = title,
        abstract = abstract,
        source = source,
        url = url,
        publicationDate = publishedDate,
        imageUrl = multimedia?.maxByOrNull { media -> media.width.toDouble() / media.height }?.url
    )
}
