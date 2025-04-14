package io.github.kroune.super_financer_ui.component.postsFeedScreen

import androidx.paging.PagingData
import io.github.kroune.super_financer_ui.events.PostsFeedScreenEvent
import io.github.kroune.super_financer_ui.ui.postsFeedScreen.PostUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PostsFeedScreenComponentI {
    val postsFeed: Flow<PagingData<PostUiModel>>
    fun onEvent(event: PostsFeedScreenEvent)
    val jwtTokenState: StateFlow<String?>
    val postTitle: String
    val postTitleValid: Boolean
    val postText: String
    val postTextValid: Boolean
    val link: String
    val linkValid: Boolean
}
