package io.github.kroune.superfinancer.mapper

import io.github.kroune.super_financer_api.domain.model.PostsFeedItem
import io.github.kroune.super_financer_api.domain.model.UserInfoResult
import io.github.kroune.super_financer_ui.ui.postsFeedScreen.PostUiModel
import io.github.kroune.superfinancer.domain.repositories.userInfoRepository.UserInfoRepositoryI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

suspend fun PostsFeedItem.toPostUiModel(
    userInfoRepository: UserInfoRepositoryI,
): PostUiModel {
    val flow: MutableStateFlow<UserInfoResult?> = MutableStateFlow(null)
    CoroutineScope(coroutineContext).launch {
        val userInfo = userInfoRepository.getUserInfo(userId)
        flow.emit(userInfo)
    }
    return PostUiModel(
        id = postId,
        isLiked = isLiked,
        title = title,
        text = text,
        tags = tags,
        images = images,
        userInfo = flow,
        attachedNewsArticle = attachedNewsArticle
    )
}
