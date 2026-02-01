package com.haero.tonestore.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.dsl.module

/**
 * Firebase 관련 의존성 Module
 */
val firebaseModule = module {
    // Firebase Auth
    single { FirebaseAuth.getInstance() }

    // Firebase Firestore
    single { FirebaseFirestore.getInstance() }

    // Firebase Storage
    single { FirebaseStorage.getInstance() }
}
