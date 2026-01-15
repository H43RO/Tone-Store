package com.haero.tonestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.haero.tonestore.presentation.navigation.ToneStoreNavGraph
import com.haero.tonestore.ui.theme.ToneStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToneStoreTheme {
                ToneStoreNavGraph()
            }
        }
    }
}
