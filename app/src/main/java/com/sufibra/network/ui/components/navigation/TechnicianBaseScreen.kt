package com.sufibra.network.ui.components.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun TechnicianBaseScreen(
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            TechnicianNavigationBar(navController)
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}