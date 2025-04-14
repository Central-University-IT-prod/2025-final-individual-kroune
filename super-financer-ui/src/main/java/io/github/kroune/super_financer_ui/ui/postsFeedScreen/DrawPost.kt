package io.github.kroune.super_financer_ui.ui.postsFeedScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import io.github.kroune.super_financer_api.domain.model.UserInfoResult
import io.github.kroune.super_financer_ui.LoadingCircle
import io.github.kroune.super_financer_ui.R
import io.github.kroune.super_financer_ui.theme.SuperFinancer
import io.github.kroune.super_financer_ui.ui.postsFeedScreen.PostsFeedScreenConstants.LINES_DISPLAYED_BY_DEFAULT

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawPost(
    index: Int,
    post: PostUiModel,
    snackbarHostState: SnackbarHostState,
    onLike: () -> Unit,
    onUnlike: () -> Unit,
    onNavigationToArticle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        border = if (post.isLiked == true) BorderStroke(2.dp, Color.Red) else null

    ) {
        Column(
            modifier = Modifier
                .combinedClickable(
                    onDoubleClick = {
                        when (post.isLiked) {
                            true -> onUnlike()
                            false -> onLike()
                            null -> {
                                // do nothing
                            }
                        }
                    },
                    onClick = {}
                )
                .padding(10.dp)
        ) {
            Text(
                post.title,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            var showExpandButton by rememberSaveable { mutableStateOf(false) }
            val showExpandButtonAnimation = remember { MutableTransitionState(showExpandButton) }
            var isTextExpanded by rememberSaveable { mutableStateOf(false) }
            Text(
                post.text,
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth(),
                overflow = TextOverflow.Ellipsis,
                maxLines = if (!isTextExpanded) LINES_DISPLAYED_BY_DEFAULT else Int.MAX_VALUE,
                onTextLayout = {
                    if (it.hasVisualOverflow) {
                        showExpandButtonAnimation.targetState = true
                        // persist this information or our "show less" button will disappear
                        // we use a separate property, because [MutableTransitionState] can't be saved using [rememberSaveable]
                        showExpandButton = true
                    }
                },
            )
            AnimatedVisibility(showExpandButtonAnimation) {
                Text(
                    if (!isTextExpanded) stringResource(R.string.show_all_text)
                    else stringResource(R.string.hide_expanded_text),
                    modifier = Modifier.clickable {
                        isTextExpanded = !isTextExpanded
                    },
                    color = SuperFinancer.fixedAccentColors.linkColor
                )
            }
            if (post.tags.isNotEmpty()) Spacer(Modifier.height(5.dp))
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start)
            ) {
                post.tags.forEach { tag ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Box(
                            modifier = Modifier.padding(3.dp),
                        ) {
                            Text(
                                tag, color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
            if (post.images.isNotEmpty()) Spacer(Modifier.height(5.dp))
            LazyRow(
                modifier = Modifier
                    .heightIn(max = 100.dp)
                    .clip(RoundedCornerShape(5))
                    .padding(bottom = 10.dp)
                    .border(3.dp, Color.Black, RoundedCornerShape(5)),
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start)
            ) {
                items(post.images, { "post-$index-image-$it" }) {
                    AsyncImage(
                        model = it,
                        contentDescription = "some useful description",
                        modifier = Modifier.clip(RoundedCornerShape(5))
                    )
                }
            }
            if (!post.attachedNewsArticle.isNullOrEmpty()) {
                Text(
                    stringResource(R.string.article_link),
                    color = SuperFinancer.fixedAccentColors.linkColor,
                    modifier = Modifier
                        .clickable {
                            onNavigationToArticle()

                        }
                )
            }
            run {
                val userInfo = post.userInfo.collectAsStateWithLifecycle().value
                val notificationText = when (userInfo) {
                    UserInfoResult.NetworkError -> {
                        stringResource(R.string.user_info_network_error)
                    }

                    UserInfoResult.ServerError -> {
                        stringResource(R.string.user_info_server_error)
                    }

                    is UserInfoResult.Success -> {
                        Text(
                            userInfo.data.login, fontSize = 10.sp
                        )
                        return@run
                    }

                    UserInfoResult.TooManyRequests -> {
                        stringResource(R.string.user_info_too_many_requests)
                    }

                    UserInfoResult.UnknownError -> {
                        stringResource(R.string.user_info_unknown_error)
                    }

                    null -> {
                        LoadingCircle(Modifier.size(20.dp))
                        return@run
                    }
                }
                Text(
                    stringResource(R.string.user_info_error),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 10.sp
                )
                LaunchedEffect(userInfo) {
                    snackbarHostState.showSnackbar(notificationText)
                }
            }
        }
    }
}
