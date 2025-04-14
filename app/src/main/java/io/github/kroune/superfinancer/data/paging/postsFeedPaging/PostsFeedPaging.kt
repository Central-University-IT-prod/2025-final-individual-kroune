package io.github.kroune.superfinancer.data.paging.postsFeedPaging

import androidx.paging.PagingState
import io.github.kroune.super_financer_api.data.sources.remote.superFinancer.postsFeedRemoteDataSource.PostsFeedRemoteDataSourceI
import io.github.kroune.super_financer_ui.ui.postsFeedScreen.PostUiModel
import io.github.kroune.superfinancer.data.sources.local.superFinancer.jwtTokenLocalDataSource.JwtTokenLocalDataSourceI
import io.github.kroune.superfinancer.domain.repositories.userInfoRepository.UserInfoRepositoryI
import io.github.kroune.superfinancer.mapper.toPostUiModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PostsFeedPaging : PostsFeedPagingI(), KoinComponent {
    private val userInfoRepository by inject<UserInfoRepositoryI>()
    private val remoteDataSource by inject<PostsFeedRemoteDataSourceI>()
    private val jwtTokenLocalDataSource by inject<JwtTokenLocalDataSourceI>()
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostUiModel> {
        return runCatching {
            val offset = params.key ?: 0

            val response = remoteDataSource.getFeed(
                offset.toLong(),
                params.loadSize,
                jwtTokenLocalDataSource.jwtTokenState.value
            ).map {
                it.toPostUiModel(userInfoRepository)
            }

            LoadResult.Page(
                data = response,
                prevKey = if (offset == 0) null else offset - 1,
                nextKey = if (response.size < params.loadSize) null else offset + 1
            )
        }.recover { e ->
            LoadResult.Error(e)
        }.getOrThrow()
    }

    override fun getRefreshKey(state: PagingState<Int, PostUiModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
