package com.haero.tonestore.presentation.ui.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.haero.tonestore.R
import com.haero.tonestore.presentation.viewmodel.LoginViewModel
import com.haero.tonestore.ui.designsystem.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { idToken ->
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    viewModel.handleIntent(LoginIntent.SignInWithGoogle(credential))
                }
            } catch (e: ApiException) {
                // 로그인 취소 또는 실패
            }
        }
    }

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn && state.currentUser != null) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.handleIntent(LoginIntent.ClearError)
        }
    }

    ObsidianBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            // Back button
            ObsidianIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onNavigateBack,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp),
                tint = Obsidian.colors.textPrimary
            )

            if (state.isLoading) {
                Box(modifier = Modifier.align(Alignment.Center)) {
                    ObsidianLoadingIndicator()
                }
            } else if (state.isLoggedIn && state.currentUser != null) {
                ObsidianProfileContent(
                    state = state,
                    onSignOut = { viewModel.handleIntent(LoginIntent.SignOut) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                )
            } else {
                ObsidianLoginContent(
                    isLoading = state.isLoading,
                    onGoogleSignIn = {
                        launcher.launch(googleSignInClient.signInIntent)
                    },
                    onAnonymousSignIn = {
                        viewModel.handleIntent(LoginIntent.SignInAnonymously)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun ObsidianLoginContent(
    isLoading: Boolean,
    onGoogleSignIn: () -> Unit,
    onAnonymousSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Obsidian Logo
        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(24.dp, CircleShape, spotColor = Obsidian.colors.primary.copy(alpha = 0.4f))
                .clip(CircleShape)
                .background(Obsidian.colors.primaryMuted)
                .border(1.5.dp, Obsidian.colors.primary.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = Obsidian.colors.primary,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Tone Store",
            style = Obsidian.typography.displayLarge,
            color = Obsidian.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.login_subtitle),
            style = Obsidian.typography.bodyLarge,
            color = Obsidian.colors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(56.dp))

        // Google Sign-In Button
        ObsidianSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            onClick = { if (!isLoading) onGoogleSignIn() },
            elevation = Obsidian.elevation.md
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Google G logo
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "G",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4285F4)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.continue_with_google),
                    style = Obsidian.typography.labelLarge,
                    color = Obsidian.colors.textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Anonymous Sign-In
        ObsidianTextButton(
            onClick = onAnonymousSignIn,
            enabled = !isLoading,
            icon = Icons.Default.Person
        ) {
            Text("익명으로 둘러보기")
        }
    }
}

@Composable
private fun ObsidianProfileContent(
    state: LoginState,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user = state.currentUser ?: return

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Profile image with gradient border
        Box(
            modifier = Modifier
                .size(108.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Obsidian.colors.primary, Obsidian.colors.primaryLight)
                    )
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
                        .background(Obsidian.colors.bgSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.displayName.firstOrNull()?.uppercase() ?: "?",
                        style = Obsidian.typography.displaySmall,
                        color = Obsidian.colors.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = user.displayName.ifEmpty { "익명 사용자" },
            style = Obsidian.typography.headlineLarge,
            color = Obsidian.colors.textPrimary
        )

        if (user.email.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user.email,
                style = Obsidian.typography.bodyMedium,
                color = Obsidian.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        ObsidianOutlinedButton(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("로그아웃")
        }
    }
}
