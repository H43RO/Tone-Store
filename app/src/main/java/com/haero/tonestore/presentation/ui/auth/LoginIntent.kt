package com.haero.tonestore.presentation.ui.auth

import com.google.firebase.auth.AuthCredential

/**
 * 로그인 화면 Intent
 */
sealed class LoginIntent {
    data class SignInWithGoogle(val credential: AuthCredential) : LoginIntent()
    data object SignInAnonymously : LoginIntent()
    data object SignOut : LoginIntent()
    data object ClearError : LoginIntent()
}
