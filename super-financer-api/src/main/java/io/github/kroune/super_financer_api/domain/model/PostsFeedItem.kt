package io.github.kroune.super_financer_api.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PostsFeedItem(
    val postId: Long,
    val isLiked: Boolean?,
    val title: String,
    val text: String,
    val tags: List<String>,
    val images: List<ByteArray>,
    val userId: Long,
    val attachedNewsArticle: String?
)

@Serializable
data class NewPostModel(
    val title: String,
    val text: String,
    val tags: List<String>,
    val images: List<ByteArray>,
    val attachedNewsArticle: String?
)
