package com.haero.tonestore.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.haero.tonestore.R
import com.haero.tonestore.presentation.ui.auth.LoginScreen
import com.haero.tonestore.presentation.ui.community.CommunityScreen
import com.haero.tonestore.presentation.ui.create.CreateToneScreen
import com.haero.tonestore.presentation.ui.detail.DetailScreen
import com.haero.tonestore.presentation.ui.home.HomeScreen
import com.haero.tonestore.presentation.ui.pedalboard.PedalBoardListScreen
import com.haero.tonestore.presentation.ui.pedalboard.PedalBoardScreen
import com.haero.tonestore.presentation.ui.preset_detail.PresetDetailScreen
import com.haero.tonestore.presentation.ui.settings.SettingsScreen
import com.haero.tonestore.presentation.ui.share.ShareToneScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    data object Main : Screen("main")

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
    data object PresetDetail : Screen("preset_detail/{presetId}") {
        fun createRoute(presetId: String): String = "preset_detail/$presetId"
    }
    data object ShareTone : Screen("share_tone/{toneSettingId}") {
        fun createRoute(toneSettingId: String): String = "share_tone/$toneSettingId"
    }
    data object Login : Screen("login")
}

sealed class BottomNavTab(
    val index: Int,
    val titleResId: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    data object Home : BottomNavTab(
        index = 0,
        titleResId = R.string.tab_home,
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )
    data object PedalBoard : BottomNavTab(
        index = 1,
        titleResId = R.string.tab_pedalboard,
        icon = Icons.Outlined.Dashboard,
        selectedIcon = Icons.Outlined.Dashboard
    )
    data object Community : BottomNavTab(
        index = 2,
        titleResId = R.string.tab_community,
        icon = Icons.Outlined.Public,
        selectedIcon = Icons.Filled.Public
    )
    data object Settings : BottomNavTab(
        index = 3,
        titleResId = R.string.tab_settings,
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings
    )
}

private val bottomNavTabs = listOf(
    BottomNavTab.Home,
    BottomNavTab.PedalBoard,
    BottomNavTab.Community,
    BottomNavTab.Settings
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ToneStoreNavGraph(navController: NavHostController = rememberNavController()) {
    val animationDuration = 300

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(route = Screen.Main.route) {
            MainTabScreen(
                onNavigateToCreate = {
                    navController.navigate(Screen.Create.createRoute())
                },
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
                },
                onNavigateToPedalBoardCreate = {
                    navController.navigate(Screen.PedalBoardEdit.createRoute())
                },
                onNavigateToPedalBoardEdit = { id ->
                    navController.navigate(Screen.PedalBoardEdit.createRoute(id))
                },
                onNavigateToPresetDetail = { presetId ->
                    navController.navigate(Screen.PresetDetail.createRoute(presetId))
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

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
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(animationDuration)
                )
            },
            popExitTransition = {
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
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(animationDuration)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(animationDuration)
                )
            }
        ) { backStackEntry ->
            val toneSettingId = backStackEntry.arguments?.getString("toneSettingId") ?: return@composable
            SharedTransitionLayout {
                DetailScreen(
                    toneSettingId = toneSettingId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.Create.createRoute(id))
                    },
                    onNavigateToShare = { id ->
                        navController.navigate(Screen.ShareTone.createRoute(id))
                    },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
        }

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
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(animationDuration)
                )
            },
            popExitTransition = {
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

        composable(
            route = Screen.PresetDetail.route,
            arguments = listOf(
                navArgument("presetId") { type = NavType.StringType }
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
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(animationDuration)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(animationDuration)
                )
            }
        ) { backStackEntry ->
            val presetId = backStackEntry.arguments?.getString("presetId") ?: return@composable
            PresetDetailScreen(
                presetId = presetId,
                currentUserId = FirebaseAuth.getInstance().currentUser?.uid,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ShareTone.route,
            arguments = listOf(
                navArgument("toneSettingId") { type = NavType.StringType }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    tween(animationDuration)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(animationDuration)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(animationDuration)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(animationDuration)
                )
            }
        ) { backStackEntry ->
            val toneSettingId = backStackEntry.arguments?.getString("toneSettingId") ?: return@composable
            ShareToneScreen(
                toneSettingId = toneSettingId,
                onNavigateBack = { navController.popBackStack() },
                onShareSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Login.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    tween(animationDuration)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(animationDuration)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(animationDuration)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(animationDuration)
                )
            }
        ) {
            LoginScreen(
                onNavigateBack = { navController.popBackStack() },
                onLoginSuccess = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MainTabScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToPedalBoardCreate: () -> Unit,
    onNavigateToPedalBoardEdit: (String) -> Unit,
    onNavigateToPresetDetail: (String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { bottomNavTabs.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            FloatingBottomNavBar(
                tabs = bottomNavTabs,
                selectedIndex = pagerState.currentPage,
                onTabSelected = { index ->
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> HomeScreen(
                        onNavigateToCreate = onNavigateToCreate,
                        onNavigateToDetail = onNavigateToDetail,
                        onNavigateToLogin = onNavigateToLogin,
                        sharedTransitionScope = null,
                        animatedVisibilityScope = null
                    )
                    1 -> PedalBoardListScreen(
                        onNavigateToCreate = onNavigateToPedalBoardCreate,
                        onNavigateToEdit = onNavigateToPedalBoardEdit,
                        onNavigateToLogin = onNavigateToLogin
                    )
                    2 -> CommunityScreen(
                        onNavigateToDetail = onNavigateToPresetDetail,
                        onNavigateToLogin = onNavigateToLogin
                    )
                    3 -> SettingsScreen(
                        onNavigateToLogin = onNavigateToLogin
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(innerPadding.calculateBottomPadding() + 24.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun FloatingBottomNavBar(
    tabs: List<BottomNavTab>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tab ->
                    FloatingNavItem(
                        tab = tab,
                        selected = selectedIndex == tab.index,
                        onClick = { onTabSelected(tab.index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingNavItem(
    tab: BottomNavTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        },
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "backgroundColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "contentColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val horizontalPadding by animateDpAsState(
        targetValue = if (selected) 16.dp else 12.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "horizontalPadding"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = horizontalPadding, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (selected) tab.selectedIcon else tab.icon,
                contentDescription = stringResource(tab.titleResId),
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )

            if (selected) {
                Text(
                    text = stringResource(tab.titleResId),
                    color = contentColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
