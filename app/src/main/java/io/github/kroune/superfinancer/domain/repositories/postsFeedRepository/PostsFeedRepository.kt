package io.github.kroune.superfinancer.domain.repositories.postsFeedRepository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import io.github.kroune.super_financer_api.data.sources.remote.superFinancer.postsFeedRemoteDataSource.PostsFeedRemoteDataSourceI
import io.github.kroune.super_financer_api.domain.model.NewPostModel
import io.github.kroune.super_financer_ui.ui.postsFeedScreen.PostUiModel
import io.github.kroune.superfinancer.data.paging.postsFeedPaging.PostsFeedPagingI
import io.github.kroune.superfinancer.domain.repositories.authRepository.AuthRepositoryI
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PostsFeedRepository : PostsFeedRepositoryI, KoinComponent {
    private lateinit var pagingSource: PostsFeedPagingI
    private val authRepository by inject<AuthRepositoryI>()
    private val postsSource by inject<PostsFeedRemoteDataSourceI>()
    override fun getPostsFeed(): Pager<Int, PostUiModel> {
        return Pager(
            config = PagingConfig(10, 10),
            pagingSourceFactory = {
                pagingSource = inject<PostsFeedPagingI>().value
                pagingSource
            }
        )
    }

    /**
     * This function should only be called after [pagingSource] has been initialized
     */
    override fun refreshPostsFeed() {
        if (::pagingSource.isInitialized)
            pagingSource.invalidate()
    }

    override suspend fun newPost(post: NewPostModel) {
        postsSource.newPost(
            post,
            authRepository.getJwtToken()!!
        )
    }

    override suspend fun likePost(postId: Long) {
        runCatching {
            postsSource.likePost(
                postId,
                authRepository.getJwtToken()!!
            )
        }
    }

    override suspend fun unlikePost(postId: Long) {
        runCatching {
            postsSource.unlikePost(
                postId,
                authRepository.getJwtToken()!!
            )
        }
    }
}
