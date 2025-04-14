package io.github.kroune.super_financer_ui.ui.postsFeedScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import io.github.kroune.super_financer_ui.R
import io.github.kroune.super_financer_ui.component.postsFeedScreen.PostsFeedScreenComponentI
import io.github.kroune.super_financer_ui.events.PostsFeedScreenEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsFeedScreen(
    component: PostsFeedScreenComponentI
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    Scaffold(snackbarHost = {
        SnackbarHost(snackbarState)
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
        ) {
            val addNewPost = remember { MutableTransitionState(false) }
            Row(
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        stringResource(R.string.news_feed_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                    OutlinedButton({
                        // this shouldn't take long, so we don't have to show any loading
                        scope.launch {
                            if (component.jwtTokenState.value == null) {
                                component.onEvent(PostsFeedScreenEvent.LogIntoAccount)
                                return@launch
                            }
                            addNewPost.targetState = true
                        }
                    }) {
                        Text(stringResource(R.string.make_new_post))
                    }
                }
                if (component.jwtTokenState.collectAsState().value != null) {
                    var showSignOutButton by remember { mutableStateOf(false) }
                    if (!showSignOutButton)
                        IconButton({
                            showSignOutButton = !showSignOutButton
                        }) {
                            Icon(painterResource(R.drawable.username), "account")
                        }
                    else
                        IconButton({
                            scope.launch {
                                component.onEvent(PostsFeedScreenEvent.LogOut)
                            }
                        }) {
                            Icon(painterResource(R.drawable.sign_out), "logout")
                        }
                }
            }
            AnimatedVisibility(addNewPost) {
                DrawNewPostDialog(
                    component.postTitle,
                    component.postTitleValid,
                    {
                        component.onEvent(PostsFeedScreenEvent.UpdateTitle(it))
                    },
                    component.postText,
                    component.postTextValid,
                    {
                        component.onEvent(PostsFeedScreenEvent.UpdateText(it))
                    },
                    component.link,
                    component.linkValid,
                    {
                        component.onEvent(PostsFeedScreenEvent.UpdateLink(it))
                    },
                    {
                        addNewPost.targetState = false
                    },
                    { data ->
                        component.onEvent(PostsFeedScreenEvent.NewPost(data))
                    }
                )
            }
            val postsItems = component.postsFeed.collectAsLazyPagingItems()
            val displaySkeleton = remember { MutableTransitionState(true) }
            var skeletonRendered by remember { mutableStateOf(false) }
            var isRefreshing by remember { mutableStateOf(false) }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    scope.launch {
                        component.onEvent(PostsFeedScreenEvent.RefreshPosts)
                        // we simply tell pager to refresh, there is nothing to wait, so we simply add some nice delay
                        delay(600)
                        isRefreshing = false
                    }
                },
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (displaySkeleton.currentState) {
                        items(10, { "skeletion-$it" }) {
                            skeletonRendered = true
                            // it has to be this way, or we will have to use an implicit receiver
                            androidx.compose.animation.AnimatedVisibility(
                                displaySkeleton,
                            ) {
                                DrawPostSkeleton()
                            }
                        }
                    }
                    items(postsItems.itemCount, key = { it }) { index ->
                        val post = postsItems[index]!!
                        DrawPost(
                            index,
                            post,
                            snackbarState,
                            { component.onEvent(PostsFeedScreenEvent.AddLike(post.id)) },
                            { component.onEvent(PostsFeedScreenEvent.RemoveLike(post.id)) },
                            { component.onEvent(PostsFeedScreenEvent.LinkClick(post.attachedNewsArticle!!)) }
                        )
                    }
                    when (postsItems.loadState.append) {
                        is LoadState.Error -> {
                        }

                        LoadState.Loading -> {
                            item {
                                DrawPostSkeleton()
                            }
                        }

                        is LoadState.NotLoading -> {
                            item {
                                if (postsItems.itemCount != 0) Text(stringResource(R.string.reached_the_end_of_the_feed))
                            }
                        }
                    }
                }
            }
            val errorMessage = stringResource(R.string.error_loading_data)
            val retryMessage = stringResource(R.string.retry)
            LaunchedEffect(postsItems.loadState) {
                if (postsItems.loadState.refresh !is LoadState.Error) return@LaunchedEffect
                val action = snackbarState.showSnackbar(errorMessage, retryMessage)
                if (action == SnackbarResult.ActionPerformed) {
                    component.onEvent(PostsFeedScreenEvent.RefreshFailedPosts)
                }
            }
            // if we update visibility state before skeleton was rendered it wouldn't hide properly
            if (skeletonRendered) {
                val value =
                    (postsItems.loadState.append is LoadState.NotLoading && postsItems.itemCount == 0)
                if (!value) displaySkeleton.targetState = false
            }
        }
    }
}

@Composable
fun DrawPostSkeleton() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val skeletonAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.6f, animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    Card(
        modifier = Modifier
            .alpha(skeletonAlpha)
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
    ) {}
}
