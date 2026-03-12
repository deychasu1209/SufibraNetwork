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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.ui.components.navigation.TechnicianBaseScreen
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.viewmodel.EventViewModel

@Composable
fun TechnicianAvailableEventsScreen(navController: NavController) {

    val viewModel: EventViewModel = viewModel()
    val events by viewModel.availableEvents.collectAsState()
    val clients by viewModel.clients.collectAsState()
    val clientsMap = clients.associateBy { it.idCliente }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.loadAvailableEvents()
        viewModel.loadClients()
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

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Revisa los eventos listos para tomar sin salir del flujo técnico.",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
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
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(events) { event ->
                    val client = event.clienteId?.let { clientsMap[it] }

                    EventCard(
                        tipo = event.tipoEvento,
                        descripcion = event.descripcion,
                        estado = event.estadoEvento,
                        prioridad = event.prioridad,
                        fecha = event.fechaCreacion,
                        idEvento = event.idEvento,
                        nombreCliente = client?.nombresApellidos,
                        direccionCliente = client?.direccion,
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
