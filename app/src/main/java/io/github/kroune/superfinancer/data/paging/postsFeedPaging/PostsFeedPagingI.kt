package io.github.kroune.superfinancer.data.paging.postsFeedPaging

import androidx.paging.PagingSource
import io.github.kroune.super_financer_ui.ui.postsFeedScreen.PostUiModel

abstract class PostsFeedPagingI : PagingSource<Int, PostUiModel>()
