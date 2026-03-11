package com.sufibra.network

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sufibra.network.ui.navigation.AppNavigation
import com.sufibra.network.ui.theme.SufibraNetworkTheme
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        setContent {
            SufibraNetworkTheme {
                AppNavigation()
            }
        }
    }
}
