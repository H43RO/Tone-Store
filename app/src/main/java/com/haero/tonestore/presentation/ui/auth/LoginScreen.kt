package com.haero.tonestore.presentation.ui.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
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
import com.haero.tonestore.ui.components.GlassBackground
import com.haero.tonestore.ui.components.GlassCard
import com.haero.tonestore.ui.components.GlassIconButton
import com.haero.tonestore.ui.components.GlassOutlinedButton
import com.haero.tonestore.ui.components.LocalEmberGlassTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = LocalEmberGlassTheme.current
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

    GlassBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            // Back button
            GlassIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onNavigateBack,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
            )

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = theme.primary
                )
            } else if (state.isLoggedIn && state.currentUser != null) {
                GlassProfileContent(
                    state = state,
                    onSignOut = { viewModel.handleIntent(LoginIntent.SignOut) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                )
            } else {
                GlassLoginContent(
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
private fun GlassLoginContent(
    isLoading: Boolean,
    onGoogleSignIn: () -> Unit,
    onAnonymousSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Glass Logo
        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(20.dp, CircleShape, spotColor = theme.primary.copy(alpha = 0.5f))
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            theme.primary.copy(alpha = 0.3f),
                            theme.primary.copy(alpha = 0.1f)
                        )
                    )
                )
                .border(1.dp, theme.primary.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = theme.primary,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Tone Store",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.login_subtitle),
            fontSize = 16.sp,
            color = theme.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(56.dp))

        // Google Sign-In Button (Glass style)
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable(enabled = !isLoading) { onGoogleSignIn() },
            cornerRadius = 16.dp,
            glassAlpha = 0.2f
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Google G logo style
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4285F4)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.continue_with_google),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = theme.textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Anonymous Sign-In
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(enabled = !isLoading) { onAnonymousSignIn() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = theme.textSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "익명으로 둘러보기",
                fontSize = 14.sp,
                color = theme.textSecondary
            )
        }
    }
}

@Composable
private fun GlassProfileContent(
    state: LoginState,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current
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
                    Brush.linearGradient(listOf(theme.primary, theme.accent))
                )
                .padding(4.dp)
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
                    Text(
                        text = user.displayName.firstOrNull()?.uppercase() ?: "?",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = user.displayName.ifEmpty { "익명 사용자" },
            fontSize = 24.sp,
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

        Spacer(modifier = Modifier.height(40.dp))

        GlassOutlinedButton(
            text = "로그아웃",
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}
