package io.github.kroune.super_financer_ui.events

import io.github.kroune.super_financer_api.domain.model.NewPostModel

sealed interface PostsFeedScreenEvent {
    data class NewPost(
        val data: NewPostModel
    ): PostsFeedScreenEvent

    data object RefreshFailedPosts: PostsFeedScreenEvent
    data object RefreshPosts: PostsFeedScreenEvent
    data object LogIntoAccount: PostsFeedScreenEvent
    data class UpdateTitle(val title: String): PostsFeedScreenEvent
    data class UpdateText(val text: String): PostsFeedScreenEvent
    data object LogOut: PostsFeedScreenEvent
    data class AddLike(val postId: Long): PostsFeedScreenEvent
    data class LinkClick(val link: String): PostsFeedScreenEvent
    data class RemoveLike(val postId: Long): PostsFeedScreenEvent
    data class UpdateLink(val link: String): PostsFeedScreenEvent
}
