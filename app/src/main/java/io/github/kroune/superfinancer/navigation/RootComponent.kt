package io.github.kroune.superfinancer.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceCurrent
import io.github.kroune.superfinancer.components.financeScreen.FinanceScreenComponent
import io.github.kroune.superfinancer.components.loginScreen.LoginScreenComponent
import io.github.kroune.superfinancer.components.mainScreen.MainScreenComponent
import io.github.kroune.superfinancer.components.postsFeedScreen.PostsFeedScreenComponent
import io.github.kroune.superfinancer.components.registerScreen.RegisterScreenComponent
import io.github.kroune.superfinancer.components.webview.WebViewScreenComponent
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    val navigation = StackNavigation<Configuration>()

    val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = getInitialConfiguration(),
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun getInitialConfiguration(): Configuration {
        return Configuration.MainScreen(scale())
    }

    /**
     * Child factory
     */
    fun createChild(
        config: Configuration,
        context: ComponentContext
    ): Child {
        return when (config) {
            is Configuration.FinanceScreen -> {
                Child.FinanceChild(
                    FinanceScreenComponent(
                        onNavigationToMainScreen = {
                            navigation.pushToFront(Configuration.MainScreen())
                        },
                        onNavigationToNewsFeedScreen = {
                            navigation.pushToFront(Configuration.NewsFeedScreen())
                        },
                        context
                    )
                )
            }

            is Configuration.MainScreen -> {
                Child.MainChild(
                    MainScreenComponent(
                        onNavigationToArticleWebView = { url: String ->
                            navigation.pushToFront(Configuration.WebViewScreen(url))
                        },
                        onNavigationToFinanceScreen = {
                            navigation.pushToFront(Configuration.FinanceScreen())
                        },
                        onNavigationToNewsFeedScreen = {
                            navigation.pushToFront(Configuration.NewsFeedScreen())
                        },
                        componentContext = context
                    )
                )
            }

            is Configuration.NewsFeedScreen -> {
                Child.NewsFeedChild(
                    PostsFeedScreenComponent(
                        config.initialLink,
                        {
                            navigation.pushToFront(Configuration.WebViewScreen(it))
                        },
                        {
                            navigation.pushToFront(Configuration.LoginScreen())
                        },
                        context
                    )
                )
            }

            is Configuration.WebViewScreen -> {
                Child.WebViewChild(
                    WebViewScreenComponent(
                        config.url,
                        {
                            navigation.pushToFront(Configuration.NewsFeedScreen(it))
                        },
                        context
                    )
                )
            }

            is Configuration.LoginScreen -> {
                Child.LoginChild(
                    LoginScreenComponent(
                        onSuccessfulAuth = {
                            navigation.pop()
                        },
                        onNavigateToRegisterScreen = {
                            navigation.replaceCurrent(Configuration.RegisterScreen())
                        },
                        context
                    )
                )
            }

            is Configuration.RegisterScreen ->
                Child.RegisterChild(
                    RegisterScreenComponent(
                        onSuccessfulAuth = {
                            navigation.pop()
                        },
                        onNavigateToLoginScreen = {
                            navigation.replaceCurrent(Configuration.LoginScreen())
                        },
                        context
                    )
                )
        }
    }

    sealed class Child(open val component: ComponentContext) {
        data class MainChild(
            override val component: MainScreenComponent
        ) : Child(component)

        data class FinanceChild(
            override val component: FinanceScreenComponent
        ) : Child(component)

        data class NewsFeedChild(
            override val component: PostsFeedScreenComponent
        ) : Child(component)

        data class LoginChild(
            override val component: LoginScreenComponent
        ) : Child(component)

        data class RegisterChild(
            override val component: RegisterScreenComponent
        ) : Child(component)

        data class WebViewChild(
            override val component: WebViewScreenComponent
        ) : Child(component)
    }

    @Serializable
    sealed class Configuration(
        @Transient
        var animation: StackAnimator = slide()
    ) {
        @Serializable
        data class MainScreen(
            @Transient
            val customAnimation: StackAnimator = slide()
        ) : Configuration(customAnimation)

        @Serializable
        data class FinanceScreen(
            @Transient
            val customAnimation: StackAnimator = slide()
        ) : Configuration(customAnimation)

        @Serializable
        data class NewsFeedScreen(
            val initialLink: String = "",
            @Transient
            val customAnimation: StackAnimator = slide()
        ) : Configuration(customAnimation)

        @Serializable
        data class LoginScreen(
            @Transient
            val customAnimation: StackAnimator = scale()
        ) : Configuration(customAnimation)

        @Serializable
        data class RegisterScreen(
            @Transient
            val customAnimation: StackAnimator = scale()
        ) : Configuration(customAnimation)

        @Serializable
        data class WebViewScreen(
            val url: String,
            @Transient
            val customAnimation: StackAnimator = slide()
        ) : Configuration(customAnimation)
    }
}
