package com.sufibra.network.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun UserInitialAvatar(
    initial: String?,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val safeInitial = initial?.trim()?.take(1)?.uppercase().orEmpty().ifBlank { "?" }

    Box(
        modifier = modifier
            .size(40.dp)
            .background(
                color = colorScheme.primary,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = safeInitial,
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}
