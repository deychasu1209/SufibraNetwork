package com.sufibra.network.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sufibra.network.viewmodel.EventViewModel
import com.sufibra.network.domain.model.Event
import com.sufibra.network.ui.components.navigation.TechnicianBaseScreen
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.ui.theme.Cards


@Composable
fun TechnicianAvailableEventsScreen(navController: NavController) {

    val viewModel: EventViewModel = viewModel()
    val events by viewModel.availableEvents.collectAsState()

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
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar evento...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("${events.size} eventos disponibles")

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

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Cards
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
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.descripcion,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.estadoEvento,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}