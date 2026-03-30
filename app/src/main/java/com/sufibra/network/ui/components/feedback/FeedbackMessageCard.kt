package com.sufibra.network.ui.components.feedback

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class FeedbackMessageType {
    ERROR,
    SUCCESS,
    INFO
}

@Composable
fun FeedbackMessageCard(
    message: String,
    type: FeedbackMessageType,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val (containerColor, contentColor) = when (type) {
        FeedbackMessageType.ERROR -> colorScheme.errorContainer to colorScheme.onErrorContainer
        FeedbackMessageType.SUCCESS -> colorScheme.primaryContainer to colorScheme.onPrimaryContainer
        FeedbackMessageType.INFO -> colorScheme.surfaceVariant to colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor
        )
    }
}
