package io.github.kroune.super_financer_api.data.sources.remote.superFinancer.postsFeedRemoteDataSource

import io.github.kroune.super_financer_api.domain.model.NewPostModel
import io.github.kroune.super_financer_api.domain.model.PostsFeedItem

interface PostsFeedRemoteDataSourceI {
    suspend fun getFeed(offset: Long, limit: Int, jwtToken: String?): List<PostsFeedItem>
    suspend fun newPost(data: NewPostModel, jwtToken: String)
    suspend fun likePost(postId: Long, jwtToken: String)
    suspend fun unlikePost(postId: Long, jwtToken: String)
}
