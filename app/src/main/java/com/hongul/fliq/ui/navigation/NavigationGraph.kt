package com.hongul.fliq.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hongul.fliq.ui.cardgen.BusinessCardGenerateScreen
import com.hongul.fliq.ui.ddu111.TagScreen
import com.hongul.fliq.ui.home.pages.CardInfoScreen
import com.hongul.fliq.ui.home.pages.CardShareScreen
import com.hongul.fliq.ui.home.pages.HomeScreen
import com.hongul.fliq.ui.hyunjin.ContactScreen
import com.hongul.fliq.ui.minjiiiiii.Chatbot_info_input

@Composable
fun ColumnScope.NavigationGraph(
    navController: NavHostController,
    showNavigation: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = NavItem.Home.route,
        modifier = Modifier.weight(1f)
    ) {
        composable(NavItem.Home.route) {
            showNavigation(true)
            HomeScreen(navigator = navController)
        }
        composable(NavItem.Contact.route) {
            showNavigation(true)
            ContactScreen {  }
        }
        composable(NavItem.Search.route) {
            showNavigation(true)
            TagScreen(onAddFriendScreen = { _, _ ->})
        }
        composable(NavItem.More.route) {
            showNavigation(true)
        }
        composable("info/{cardId}") { backStackEntry ->
            showNavigation(false)
            val cardId = backStackEntry.arguments?.getInt("cardId")
            if (cardId != null) {
                CardInfoScreen(navigator = navController, cardId = cardId)
            }
        }
        composable("cardgen") {
            showNavigation(false)
            BusinessCardGenerateScreen(navigator = navController)
        }
        composable("chatbot") {
            showNavigation(false)
            Chatbot_info_input()
        }
        composable(
            "share",
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        500,
                        easing = Ease
                    )
                ) + slideIntoContainer(
                    animationSpec = tween(
                        500,
                        easing = Ease
                    ),
                    towards = AnimatedContentTransitionScope.SlideDirection.Up
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        500,
                        easing = Ease
                    )
                ) + slideOutOfContainer(
                    animationSpec = tween(
                        500,
                        easing = Ease
                    ),
                    towards = AnimatedContentTransitionScope.SlideDirection.Down
                )
            },
        ) {
            showNavigation(false)
            CardShareScreen(navigator = navController)
        }
    }
}