package io.github.kroune.superfinancer.domain.repositories.postsFeedRepository

import androidx.paging.Pager
import io.github.kroune.super_financer_api.domain.model.NewPostModel
import io.github.kroune.super_financer_ui.ui.postsFeedScreen.PostUiModel

interface PostsFeedRepositoryI {
    fun getPostsFeed(): Pager<Int, PostUiModel>
    fun refreshPostsFeed()
    suspend fun newPost(post: NewPostModel)
    suspend fun likePost(postId: Long)
    suspend fun unlikePost(postId: Long)
}
