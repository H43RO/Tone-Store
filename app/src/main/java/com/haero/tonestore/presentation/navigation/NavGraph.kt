package com.haero.tonestore.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.haero.tonestore.presentation.ui.create.CreateToneScreen
import com.haero.tonestore.presentation.ui.detail.DetailScreen
import com.haero.tonestore.presentation.ui.home.HomeScreen
import com.haero.tonestore.presentation.ui.pedalboard.PedalBoardScreen

/**
 * 네비게이션 라우트 정의
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Create : Screen("create?editingId={editingId}") {
        fun createRoute(editingId: String? = null): String {
            return if (editingId != null) "create?editingId=$editingId" else "create"
        }
    }
    data object Detail : Screen("detail/{toneSettingId}") {
        fun createRoute(toneSettingId: String): String = "detail/$toneSettingId"
    }
    data object PedalBoard : Screen("pedalboard?editingId={editingId}") {
        fun createRoute(editingId: String? = null): String {
            return if (editingId != null) "pedalboard?editingId=$editingId" else "pedalboard"
        }
    }
}

/**
 * 앱 네비게이션 그래프
 */
@Composable
fun ToneStoreNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val animationDuration = 300
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // 홈 화면
        composable(
            route = Screen.Home.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(animationDuration)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(animationDuration)
                )
            }
        ) {
            HomeScreen(
                onNavigateToCreate = {
                    navController.navigate(Screen.Create.createRoute())
                },
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
                },
                onNavigateToPedalBoard = {
                    navController.navigate(Screen.PedalBoard.createRoute())
                }
            )
        }
        
        // 생성/편집 화면
        composable(
            route = Screen.Create.route,
            arguments = listOf(
                navArgument("editingId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(animationDuration)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(animationDuration)
                )
            }
        ) { backStackEntry ->
            val editingId = backStackEntry.arguments?.getString("editingId")
            CreateToneScreen(
                onNavigateBack = { navController.popBackStack() },
                editingId = editingId
            )
        }
        
        // 상세 화면
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("toneSettingId") { type = NavType.StringType }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(animationDuration)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(animationDuration)
                )
            }
        ) { backStackEntry ->
            val toneSettingId = backStackEntry.arguments?.getString("toneSettingId") ?: return@composable
            DetailScreen(
                toneSettingId = toneSettingId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.Create.createRoute(id))
                }
            )
        }
        
        // 페달보드 생성/편집 화면
        composable(
            route = Screen.PedalBoard.route,
            arguments = listOf(
                navArgument("editingId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(animationDuration)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(animationDuration)
                )
            }
        ) { backStackEntry ->
            val editingId = backStackEntry.arguments?.getString("editingId")
            PedalBoardScreen(
                onNavigateBack = { navController.popBackStack() },
                editingId = editingId
            )
        }
    }
}
