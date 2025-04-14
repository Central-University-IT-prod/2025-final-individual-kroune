package io.github.kroune.superfinancer.components.postsFeedScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.super_financer_ui.component.postsFeedScreen.PostsFeedScreenComponentI
import io.github.kroune.super_financer_ui.events.PostsFeedScreenEvent
import io.github.kroune.super_financer_ui.ui.postsFeedScreen.PostUiModel
import io.github.kroune.superfinancer.componentCoroutineScope
import io.github.kroune.superfinancer.domain.repositories.authRepository.AuthRepositoryI
import io.github.kroune.superfinancer.domain.repositories.postsFeedRepository.PostsFeedRepositoryI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PostsFeedScreenComponent(
    initialLink: String = "",
    val onNavigationToArticleWebView: (url: String) -> Unit,
    val onNavigationToLoginScreen: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, KoinComponent, PostsFeedScreenComponentI {
    private val componentScope = componentCoroutineScope()
    private val postsFeedRepository by inject<PostsFeedRepositoryI>()
    private val jwtTokenRepository by inject<AuthRepositoryI>()

    override val jwtTokenState = jwtTokenRepository.jwtTokenState
    override var postTitle: String by mutableStateOf("")
    override val postTitleValid: Boolean
        get() = postTitle.isNotBlank() && postTitle.length in 1..40
    override var postText: String by mutableStateOf("")
    override val postTextValid: Boolean
        get() = postText.isNotBlank() && postText.length in 1..1024
    override var link: String by mutableStateOf(initialLink)
    override val linkValid: Boolean
        get() = link.startsWith("https://www.nytimes.com/")

    override val postsFeed: Flow<PagingData<PostUiModel>> = postsFeedRepository
        .getPostsFeed()
        .flow
        .cachedIn(componentScope)

    override fun onEvent(event: PostsFeedScreenEvent) {
        when (event) {
            is PostsFeedScreenEvent.NewPost -> {
                componentScope.launch {
                    postsFeedRepository.newPost(event.data)
                    postsFeedRepository.refreshPostsFeed()
                }
            }

            PostsFeedScreenEvent.RefreshFailedPosts -> {
                postsFeedRepository.refreshPostsFeed()
            }

            // rn both of the events trigger the same action, but that can change in the future
            PostsFeedScreenEvent.RefreshPosts -> {
                postsFeedRepository.refreshPostsFeed()
            }

            PostsFeedScreenEvent.LogIntoAccount -> {
                onNavigationToLoginScreen()
            }

            is PostsFeedScreenEvent.UpdateText -> {
                postText = event.text
            }

            is PostsFeedScreenEvent.UpdateTitle -> {
                postTitle = event.title
            }

            PostsFeedScreenEvent.LogOut -> {
                componentScope.launch {
                    jwtTokenRepository.removeJwtToken()
                }
            }

            is PostsFeedScreenEvent.AddLike -> {
                if (jwtTokenState.value == null) {
                    onNavigationToLoginScreen()
                } else {
                    componentScope.launch {
                        postsFeedRepository.likePost(event.postId)
                        postsFeedRepository.refreshPostsFeed()
                    }
                }
            }

            is PostsFeedScreenEvent.RemoveLike -> {
                if (jwtTokenState.value == null) {
                    onNavigationToLoginScreen()
                } else {
                    componentScope.launch {
                        postsFeedRepository.unlikePost(event.postId)
                        postsFeedRepository.refreshPostsFeed()
                    }
                }
            }

            is PostsFeedScreenEvent.UpdateLink -> {
                link = event.link
            }

            is PostsFeedScreenEvent.LinkClick -> {
                onNavigationToArticleWebView(event.link)
            }
        }
    }
}
