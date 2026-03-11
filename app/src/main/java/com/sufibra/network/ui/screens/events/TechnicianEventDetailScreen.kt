package com.sufibra.network.ui.screens.events

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
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
        Text("Evento no encontrado")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Detalle del Evento",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Tipo: ${event.tipoEvento}")
        Text("Estado: ${event.estadoEvento}")
        Text("Descripción: ${event.descripcion}")
        Text("Prioridad: ${event.prioridad}")

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

                            event?.let {
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

    errorMessage?.let { message ->

        if (showRestrictionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError()
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
                    onClick = { viewModel.clearError()
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