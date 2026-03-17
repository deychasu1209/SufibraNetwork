package com.sufibra.network.ui.components.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FormIntroCard(
    title: String,
    subtitle: String
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun UserSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
fun RoleOptionCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = if (selected) colorScheme.primaryContainer else colorScheme.surface,
        tonalElevation = if (selected) 0.dp else 1.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (selected) colorScheme.primary else colorScheme.outlineVariant
                ) {
                    Spacer(
                        modifier = Modifier
                            .width(10.dp)
                            .height(10.dp)
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (selected) colorScheme.onPrimaryContainer else colorScheme.onSurface
                )
            }

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant
            )
        }
    }
}
