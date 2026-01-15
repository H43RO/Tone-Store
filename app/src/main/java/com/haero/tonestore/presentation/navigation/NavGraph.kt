package com.haero.tonestore.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.haero.tonestore.R
import com.haero.tonestore.presentation.ui.create.CreateToneScreen
import com.haero.tonestore.presentation.ui.detail.DetailScreen
import com.haero.tonestore.presentation.ui.home.HomeScreen
import com.haero.tonestore.presentation.ui.pedalboard.PedalBoardListScreen
import com.haero.tonestore.presentation.ui.pedalboard.PedalBoardScreen

/**
 * 네비게이션 라우트 정의
 */
sealed class Screen(val route: String) {
    // 탭 화면들
    data object Home : Screen("home")
    data object PedalBoardList : Screen("pedalboard_list")
    
    // 상세 화면들
    data object Create : Screen("create?editingId={editingId}") {
        fun createRoute(editingId: String? = null): String {
            return if (editingId != null) "create?editingId=$editingId" else "create"
        }
    }
    data object Detail : Screen("detail/{toneSettingId}") {
        fun createRoute(toneSettingId: String): String = "detail/$toneSettingId"
    }
    data object PedalBoardEdit : Screen("pedalboard_edit?editingId={editingId}") {
        fun createRoute(editingId: String? = null): String {
            return if (editingId != null) "pedalboard_edit?editingId=$editingId" else "pedalboard_edit"
        }
    }
}

/**
 * 하단 네비게이션 탭 정의
 */
sealed class BottomNavTab(
    val route: String,
    val titleResId: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    data object Home : BottomNavTab(
        route = Screen.Home.route,
        titleResId = R.string.tab_home,
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )
    data object PedalBoard : BottomNavTab(
        route = Screen.PedalBoardList.route,
        titleResId = R.string.tab_pedalboard,
        icon = Icons.Outlined.Dashboard,
        selectedIcon = Icons.Outlined.Dashboard
    )
}

private val bottomNavTabs = listOf(
    BottomNavTab.Home,
    BottomNavTab.PedalBoard
)

/**
 * 앱 네비게이션 그래프 (하단 네비게이션 포함)
 */
@Composable
fun ToneStoreNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // 하단 네비게이션 표시 여부 (탭 화면에서만 표시)
    val showBottomBar = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.PedalBoardList.route
    )
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavTabs.forEach { tab ->
                        val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.selectedIcon else tab.icon,
                                    contentDescription = stringResource(tab.titleResId)
                                )
                            },
                            label = { Text(stringResource(tab.titleResId)) },
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            val animationDuration = 300
            
            // 홈 화면 (탭)
            composable(route = Screen.Home.route) {
                HomeScreen(
                    onNavigateToCreate = {
                        navController.navigate(Screen.Create.createRoute())
                    },
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.Detail.createRoute(id))
                    }
                )
            }
            
            // 페달보드 목록 화면 (탭)
            composable(route = Screen.PedalBoardList.route) {
                PedalBoardListScreen(
                    onNavigateToCreate = {
                        navController.navigate(Screen.PedalBoardEdit.createRoute())
                    },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.PedalBoardEdit.createRoute(id))
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
                route = Screen.PedalBoardEdit.route,
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
}
