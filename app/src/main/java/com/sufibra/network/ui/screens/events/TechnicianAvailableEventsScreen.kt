package com.sufibra.network.ui.screens.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.domain.model.Event
import com.sufibra.network.ui.components.navigation.TechnicianBaseScreen
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.viewmodel.EventViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TechnicianAvailableEventsScreen(navController: NavController) {

    val viewModel: EventViewModel = viewModel()
    val events by viewModel.availableEvents.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.loadAvailableEvents()
    }

    TechnicianBaseScreen(navController) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "Eventos Disponibles",
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar evento...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${events.size} eventos disponibles",
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(events) { event ->
                    TechnicianEventCard(
                        event = event,
                        onClick = {
                            navController.navigate(
                                Screen.TechnicianEventDetail.createRoute(event.idEvento)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TechnicianEventCard(
    event: Event,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text = event.tipoEvento,
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.estadoEvento,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
