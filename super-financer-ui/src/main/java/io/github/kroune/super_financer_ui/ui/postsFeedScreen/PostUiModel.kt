package io.github.kroune.super_financer_ui.ui.postsFeedScreen

import io.github.kroune.super_financer_api.domain.model.UserInfoResult
import kotlinx.coroutines.flow.MutableStateFlow

data class PostUiModel(
    val id: Long,
    val isLiked: Boolean?,
    val title: String,
    val text: String,
    val tags: List<String>,
    val images: List<ByteArray>,
    val userInfo: MutableStateFlow<UserInfoResult?>,
    val attachedNewsArticle: String?
)
