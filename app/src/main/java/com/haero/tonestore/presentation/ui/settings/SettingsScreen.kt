package com.haero.tonestore.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.haero.tonestore.R
import com.haero.tonestore.presentation.viewmodel.SettingsViewModel
import com.haero.tonestore.ui.components.GlassBackground
import com.haero.tonestore.ui.components.GlassButton
import com.haero.tonestore.ui.components.GlassCard
import com.haero.tonestore.ui.components.GlassOutlinedButton
import com.haero.tonestore.ui.components.GlassTextField
import com.haero.tonestore.ui.components.LocalEmberGlassTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = LocalEmberGlassTheme.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.navigateToLogin) {
        if (state.navigateToLogin) {
            onNavigateToLogin()
            viewModel.handleIntent(SettingsIntent.NavigationHandled)
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.handleIntent(SettingsIntent.ClearError)
        }
    }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            snackbarHostState.showSnackbar("프로필이 저장되었습니다")
            viewModel.handleIntent(SettingsIntent.ClearError)
        }
    }

    GlassBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                GlassSettingsHeader()

                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = theme.primary)
                    }
                } else if (state.isLoggedIn && state.currentUser != null) {
                    GlassLoggedInContent(
                        state = state,
                        onNicknameChange = { viewModel.handleIntent(SettingsIntent.UpdateNickname(it)) },
                        onSaveProfile = { viewModel.handleIntent(SettingsIntent.SaveProfile) },
                        onSignOut = { viewModel.handleIntent(SettingsIntent.SignOut) }
                    )
                } else {
                    GlassNotLoggedInContent(
                        onLoginClick = { viewModel.handleIntent(SettingsIntent.NavigateToLogin) }
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
            )
        }
    }
}

@Composable
private fun GlassSettingsHeader(
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.settings),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
        )
    }
}

@Composable
private fun GlassLoggedInContent(
    state: SettingsState,
    onNicknameChange: (String) -> Unit,
    onSaveProfile: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current
    val user = state.currentUser ?: return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 프로필 카드
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            glassAlpha = 0.15f
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 프로필 이미지 with gradient border
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(theme.primary, theme.accent))
                        )
                        .padding(3.dp)
                ) {
                    if (user.photoUrl != null) {
                        AsyncImage(
                            model = user.photoUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(theme.surfaceElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = theme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = user.communityDisplayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary
                )

                if (user.email.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user.email,
                        fontSize = 14.sp,
                        color = theme.textSecondary
                    )
                }
            }
        }

        // 커뮤니티 프로필 설정
        GlassSettingsSection(
            title = "커뮤니티 프로필",
            icon = Icons.Default.Person
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GlassTextField(
                    value = state.nickname,
                    onValueChange = onNicknameChange,
                    label = "닉네임",
                    placeholder = "커뮤니티에서 사용할 닉네임",
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.nickname_hint),
                    fontSize = 12.sp,
                    color = theme.textMuted
                )

                GlassButton(
                    text = "저장",
                    onClick = onSaveProfile,
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 계정 설정
        GlassSettingsSection(
            title = "계정",
            icon = Icons.Default.Settings
        ) {
            GlassOutlinedButton(
                text = "로그아웃",
                onClick = onSignOut,
                icon = Icons.AutoMirrored.Filled.Logout,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun GlassNotLoggedInContent(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            theme.primary.copy(alpha = 0.2f),
                            theme.primary.copy(alpha = 0.05f)
                        )
                    )
                )
                .border(1.dp, theme.primary.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = theme.primary
            )
        }

        Text(
            text = stringResource(R.string.login_required),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
        )

        Text(
            text = stringResource(R.string.login_required_settings_message),
            fontSize = 14.sp,
            color = theme.textSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        GlassButton(
            text = "로그인",
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(0.6f)
        )
    }
}

@Composable
private fun GlassSettingsSection(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val theme = LocalEmberGlassTheme.current

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        glassAlpha = 0.12f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = theme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = theme.textPrimary
                )
            }

            content()
        }
    }
}
