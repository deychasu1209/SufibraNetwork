package com.sufibra.network.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.sufibra.network.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopBar(
    title: String,
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme

    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colorScheme.surfaceVariant)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_atras),
                    contentDescription = "Volver",
                    tint = colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colorScheme.surface,
            titleContentColor = colorScheme.onSurface,
            navigationIconContentColor = colorScheme.onSurface
        ),
        windowInsets = WindowInsets(0, 0, 0, 0)
    )
}
