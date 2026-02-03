package com.haero.tonestore.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.haero.tonestore.R
import com.haero.tonestore.presentation.viewmodel.SettingsViewModel
import com.haero.tonestore.ui.designsystem.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
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

    ObsidianBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ObsidianSettingsHeader()

                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        ObsidianLoadingIndicator()
                    }
                } else if (state.isLoggedIn && state.currentUser != null) {
                    ObsidianLoggedInContent(
                        state = state,
                        onNicknameChange = { viewModel.handleIntent(SettingsIntent.UpdateNickname(it)) },
                        onSaveProfile = { viewModel.handleIntent(SettingsIntent.SaveProfile) },
                        onSignOut = { viewModel.handleIntent(SettingsIntent.SignOut) }
                    )
                } else {
                    ObsidianNotLoggedInContent(
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
private fun ObsidianSettingsHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = Obsidian.spacing.screenPadding)
            .padding(top = Obsidian.spacing.lg, bottom = Obsidian.spacing.sm)
    ) {
        Text(
            text = stringResource(R.string.settings),
            style = Obsidian.typography.displaySmall,
            color = Obsidian.colors.textPrimary
        )
    }
}

@Composable
private fun ObsidianLoggedInContent(
    state: SettingsState,
    onNicknameChange: (String) -> Unit,
    onSaveProfile: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user = state.currentUser ?: return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Obsidian.spacing.screenPadding)
            .padding(top = Obsidian.spacing.md),
        verticalArrangement = Arrangement.spacedBy(Obsidian.spacing.sectionGap)
    ) {
        // 프로필 요약 카드
        ObsidianCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 프로필 이미지 with gradient border
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Obsidian.colors.primary, Obsidian.colors.primaryLight)
                            )
                        )
                        .padding(2.dp)
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
                                .background(Obsidian.colors.bgSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = Obsidian.colors.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Obsidian.spacing.lg))

                Text(
                    text = user.communityDisplayName,
                    style = Obsidian.typography.headlineLarge,
                    color = Obsidian.colors.textPrimary
                )

                if (user.email.isNotBlank()) {
                    Spacer(modifier = Modifier.height(Obsidian.spacing.xs))
                    Text(
                        text = user.email,
                        style = Obsidian.typography.bodyMedium,
                        color = Obsidian.colors.textSecondary
                    )
                }
            }
        }

        // 커뮤니티 프로필 설정 섹션
        ObsidianSettingsSection(
            title = "커뮤니티 프로필",
            icon = Icons.Default.Person
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Obsidian.spacing.lg)
            ) {
                ObsidianTextField(
                    value = state.nickname,
                    onValueChange = onNicknameChange,
                    placeholder = "닉네임",
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.nickname_hint),
                    style = Obsidian.typography.caption,
                    color = Obsidian.colors.textMuted
                )

                ObsidianButton(
                    onClick = onSaveProfile,
                    enabled = !state.isSaving,
                    isLoading = state.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("설정 저장")
                }
            }
        }

        // 계정 설정 섹션
        ObsidianSettingsSection(
            title = "계정",
            icon = Icons.Default.Settings
        ) {
            ObsidianOutlinedButton(
                onClick = onSignOut,
                icon = Icons.AutoMirrored.Filled.Logout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("로그아웃")
            }
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
private fun ObsidianNotLoggedInContent(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Obsidian.spacing.screenPadding)
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Obsidian.spacing.lg)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Obsidian.colors.primaryMuted)
                .border(1.dp, Obsidian.colors.primary.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Obsidian.colors.primary
            )
        }

        Text(
            text = stringResource(R.string.login_required),
            style = Obsidian.typography.headlineMedium,
            color = Obsidian.colors.textPrimary
        )

        Text(
            text = stringResource(R.string.login_required_settings_message),
            style = Obsidian.typography.bodyMedium,
            color = Obsidian.colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(Obsidian.spacing.md))

        ObsidianButton(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("로그인하러 가기")
        }
    }
}

@Composable
private fun ObsidianSettingsSection(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Obsidian.spacing.md)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Obsidian.colors.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(Obsidian.spacing.sm))
            Text(
                text = title,
                style = Obsidian.typography.headlineSmall,
                color = Obsidian.colors.textPrimary
            )
        }

        ObsidianCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}
