package com.mpesa.tracker.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object NavRoutes {
    const val OVERVIEW = "overview"
    const val ACTIVITY = "activity?categoryId={categoryId}&nature={nature}"
    const val PLAN = "plan?tab={tab}"
    const val INSIGHTS = "insights"
    const val CHAT = "chat"
    const val SETTINGS = "settings"

    fun activity(categoryId: Int? = null, nature: String? = null): String {
        val catPart = if (categoryId != null) "categoryId=$categoryId" else "categoryId=-999"
        val naturePart = "nature=${nature ?: "NONE"}"
        return "activity?$catPart&$naturePart"
    }

    fun plan(tab: String? = null): String = "plan?tab=${tab ?: "DEFAULT"}"
}

private data class BottomNavItem(
    val baseRoute: String,
    val defaultRoute: String,
    val label: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(NavRoutes.OVERVIEW, NavRoutes.OVERVIEW, "Overview", Icons.Filled.Home),
    BottomNavItem("activity", NavRoutes.activity(), "Activity", Icons.AutoMirrored.Filled.ReceiptLong),
    BottomNavItem("plan", NavRoutes.plan(), "Plan", Icons.Filled.AccountBalanceWallet),
    BottomNavItem(NavRoutes.INSIGHTS, NavRoutes.INSIGHTS, "Insights", Icons.Filled.Analytics)
)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    val showBottomNav = currentRoute == NavRoutes.OVERVIEW ||
        currentRoute.startsWith("activity") ||
        currentRoute.startsWith("plan") ||
        currentRoute == NavRoutes.INSIGHTS

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected = when (item.baseRoute) {
                            NavRoutes.OVERVIEW -> currentRoute == NavRoutes.OVERVIEW
                            "activity" -> currentRoute.startsWith("activity")
                            "plan" -> currentRoute.startsWith("plan")
                            NavRoutes.INSIGHTS -> currentRoute == NavRoutes.INSIGHTS
                            else -> false
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) {
                                    navController.navigate(item.defaultRoute) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.OVERVIEW,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.OVERVIEW) {
                com.mpesa.tracker.ui.overview.OverviewScreen(
                    onNavigateToActivity = { catId, nature ->
                        navController.navigate(NavRoutes.activity(categoryId = catId, nature = nature)) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToPlan = { openBills ->
                        navController.navigate(NavRoutes.plan(if (openBills) "BILLS" else "BUDGETS")) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToInsights = {
                        navController.navigate(NavRoutes.INSIGHTS) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToSettings = { navController.navigate(NavRoutes.SETTINGS) }
                )
            }

            composable(
                route = NavRoutes.ACTIVITY,
                arguments = listOf(
                    navArgument("categoryId") { type = NavType.IntType; defaultValue = -999 },
                    navArgument("nature") { type = NavType.StringType; defaultValue = "NONE" }
                )
            ) { backStackEntry ->
                val catArg = backStackEntry.arguments?.getInt("categoryId") ?: -999
                val natureArg = backStackEntry.arguments?.getString("nature")?.takeIf { it != "NONE" }
                com.mpesa.tracker.ui.transactions.TransactionsScreen(
                    initialCategoryId = if (catArg == -999) null else catArg,
                    initialNature = natureArg
                )
            }

            composable(
                route = NavRoutes.PLAN,
                arguments = listOf(
                    navArgument("tab") { type = NavType.StringType; defaultValue = "DEFAULT" }
                )
            ) { backStackEntry ->
                val initialTab = backStackEntry.arguments?.getString("tab")?.takeIf { it != "DEFAULT" }
                com.mpesa.tracker.ui.plan.PlanScreen(
                    initialTab = initialTab,
                    onNavigateToTransactions = { categoryId ->
                        navController.navigate(NavRoutes.activity(categoryId = categoryId)) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(NavRoutes.INSIGHTS) {
                com.mpesa.tracker.ui.analytics.AnalyticsScreen(
                    onNavigateToTransactions = { categoryId ->
                        navController.navigate(NavRoutes.activity(categoryId = categoryId)) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToChat = { navController.navigate(NavRoutes.CHAT) }
                )
            }

            composable(
                route = NavRoutes.CHAT,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(280)) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(280)) }
            ) {
                com.mpesa.tracker.ui.chat.AiChatScreen(
                    currentBalance = "",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = NavRoutes.SETTINGS,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(250)) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(250)) }
            ) {
                com.mpesa.tracker.ui.settings.SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
