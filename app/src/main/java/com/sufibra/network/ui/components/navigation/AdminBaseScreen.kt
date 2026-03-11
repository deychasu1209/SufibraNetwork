package com.sufibra.network.ui.components.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun AdminBaseScreen(
    navController: NavController,
    floatingActionButton: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            AdminNavigationBar(navController)
        },
        floatingActionButton = {
            floatingActionButton?.invoke()
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}