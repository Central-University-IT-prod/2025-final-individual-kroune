package io.github.kroune.superfinancer.ui.mainScreen

data class ArticleUiModel(
    val title: String,
    val abstract: String,
    val source: String?,
    val url: String,
    val publicationDate: String,
    val imageUrl: String?
)
