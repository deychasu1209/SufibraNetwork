package com.sufibra.network.ui.screens.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.viewmodel.EventViewModel

@Composable
fun TechnicianEventDetailScreen(
    navController: NavController,
    eventId: String
) {

    val viewModel: EventViewModel = viewModel()
    val events by viewModel.availableEvents.collectAsState()
    var showTakeEventDialog by remember { mutableStateOf(false) }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val takeEventSuccess by viewModel.takeEventSuccess.collectAsState()
    var showRestrictionDialog by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.loadAvailableEvents()
    }

    LaunchedEffect(takeEventSuccess) {
        when (takeEventSuccess) {
            true -> {
                viewModel.clearTakeEventState()
                navController.navigate(Screen.TechnicianCurrentJob.route)
            }
            false -> {
                viewModel.clearTakeEventState()
                showRestrictionDialog = true
            }
            null -> Unit
        }
    }

    val event = events.find { it.idEvento == eventId }

    if (event == null) {
        Text(
            text = "Evento no encontrado",
            color = colorScheme.onSurfaceVariant
        )
        return
    }

    Scaffold(
        containerColor = colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text(
                text = "Detalle del Evento",
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tipo: ${event.tipoEvento}",
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = "Estado: ${event.estadoEvento}",
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Descripción: ${event.descripcion}",
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = "Prioridad: ${event.prioridad}",
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    showTakeEventDialog = true
                }
            ) {
                Text("Tomar Evento")
            }
            if (showTakeEventDialog) {

                AlertDialog(
                    onDismissRequest = {
                        showTakeEventDialog = false
                    },
                    title = {
                        Text("Confirmar acción")
                    },
                    text = {
                        Text("¿Deseas tomar este evento?")
                    },
                    confirmButton = {

                        TextButton(
                            onClick = {

                                val technicianId = FirebaseAuth
                                    .getInstance()
                                    .currentUser
                                    ?.uid ?: return@TextButton

                                event.let {
                                    viewModel.takeEvent(it, technicianId)
                                }
                                showTakeEventDialog = false
                            }
                        ) {
                            Text("Confirmar")
                        }

                    },
                    dismissButton = {

                        TextButton(
                            onClick = {
                                showTakeEventDialog = false
                            }
                        ) {
                            Text("Cancelar")
                        }

                    }
                )
            }
        }
    }

    errorMessage?.let { message ->

        if (showRestrictionDialog) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.clearError()
                },
                title = { Text("Aviso") },
                text = {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearError()
                            navController.navigate(Screen.TechnicianCurrentJob.route)
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}
