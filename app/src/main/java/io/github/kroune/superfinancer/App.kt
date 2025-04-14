package io.github.kroune.superfinancer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.pushToFront
import io.github.kroune.super_financer_ui.theme.SuperFinancerTheme
import io.github.kroune.super_financer_ui.ui.postsFeedScreen.NewsFeedScreen
import io.github.kroune.superfinancer.navigation.RootComponent
import io.github.kroune.superfinancer.navigation.RootComponent.Configuration
import io.github.kroune.superfinancer.ui.financeScreen.FinanceScreen
import io.github.kroune.superfinancer.ui.loginScreen.LoginScreen
import io.github.kroune.superfinancer.ui.mainScreen.MainScreen
import io.github.kroune.superfinancer.ui.registerScreen.RegisterScreen
import io.github.kroune.superfinancer.ui.webView.WebViewPage

@Composable
fun App(component: RootComponent) {
    val stackAnimation =
        stackAnimation<Configuration, RootComponent.Child> { child ->
            child.configuration.animation
        }
    SuperFinancerTheme {
        val childStack by component.childStack.subscribeAsState()
        val navigation = component.navigation
        Scaffold(
            bottomBar = {
                val configurationsToShowBottomBar = remember {
                    listOf(
                        Configuration.MainScreen::class.java,
                        Configuration.FinanceScreen::class.java,
                        Configuration.NewsFeedScreen::class.java
                    )
                }
                if (configurationsToShowBottomBar.any {
                        childStack.active.configuration::class.java == it
                    }
                )
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                    ) {
                        NavigationBarItem(
                            selected = childStack.active.configuration is Configuration.WebViewScreen,
                            onClick = {
                                val animation = when (childStack.active.configuration) {
                                    is Configuration.WebViewScreen -> {
                                        scale()
                                    }

                                    is Configuration.MainScreen -> {
                                        return@NavigationBarItem
                                    }

                                    else -> {
                                        customSlide(invertDirection = true)
                                    }
                                }
                                navigation.bringToFront(Configuration.MainScreen(animation))
                            },
                            icon = {
                                Icon(
                                    painterResource(
                                        R.drawable.main_icon
                                    ),
                                    "main icon",
                                )
                            },
                            label = { Text(stringResource(R.string.main_title)) }
                        )
                        NavigationBarItem(
                            selected = childStack.active.configuration is Configuration.FinanceScreen,
                            {
                                val animation = when (childStack.active.configuration) {
                                    is Configuration.MainScreen -> {
                                        slide()
                                    }

                                    is Configuration.FinanceScreen -> {
                                        return@NavigationBarItem
                                    }

                                    is Configuration.NewsFeedScreen -> {
                                        customSlide(invertDirection = true)
                                    }

                                    else -> {
                                        scale()
                                    }
                                }
                                navigation.pushToFront(Configuration.FinanceScreen(animation))
                            },
                            icon = {
                                Icon(
                                    painterResource(
                                        R.drawable.finance_icon
                                    ),
                                    "finance icon"
                                )
                            },
                            label = {
                                Text(stringResource(R.string.finance_title))
                            }
                        )
                        NavigationBarItem(
                            selected = childStack.active.configuration is Configuration.NewsFeedScreen,
                            {
                                val animation = when (childStack.active.configuration) {
                                    is Configuration.NewsFeedScreen -> {
                                        return@NavigationBarItem
                                    }

                                    else -> {
                                        slide()
                                    }
                                }
                                navigation.pushToFront(Configuration.NewsFeedScreen(customAnimation = animation))
                            },
                            icon = {
                                Icon(
                                    painterResource(
                                        R.drawable.news_icon
                                    ),
                                    "news icon",
                                )
                            },
                            label = {
                                Text(stringResource(R.string.news_feed_title))
                            }
                        )
                    }
            },
            modifier = Modifier.safeDrawingPadding()
        ) { padding ->
            Children(
                stack = childStack,
                animation = stackAnimation
            ) { child ->
                Box(modifier = Modifier.padding(padding)) {
                    when (val instance = child.instance) {
                        is RootComponent.Child.MainChild -> {
                            MainScreen(
                                instance.component,
                            )
                        }

                        is RootComponent.Child.FinanceChild -> {
                            FinanceScreen(
                                instance.component
                            )
                        }

                        is RootComponent.Child.NewsFeedChild -> {
                            NewsFeedScreen(instance.component)
                        }

                        is RootComponent.Child.WebViewChild -> {
                            WebViewPage(instance.component)
                        }

                        is RootComponent.Child.LoginChild -> {
                            LoginScreen(instance.component)
                        }

                        is RootComponent.Child.RegisterChild -> {
                            RegisterScreen(instance.component)
                        }
                    }
                }
            }
        }
    }
}
