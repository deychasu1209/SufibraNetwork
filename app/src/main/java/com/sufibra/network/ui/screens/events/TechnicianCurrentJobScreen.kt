package com.sufibra.network.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sufibra.network.domain.model.Event
import com.sufibra.network.ui.components.navigation.TechnicianNavigationBar
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.viewmodel.EventViewModel

@Composable
fun TechnicianCurrentJobScreen(
    navController: NavController
) {

    val viewModel: EventViewModel = viewModel()
    val currentEvent by viewModel.currentTechnicianEvent.collectAsState()
    val technicianId = FirebaseAuth.getInstance().currentUser?.uid
    val startEventSuccess by viewModel.startEventSuccess.collectAsState()
    var showStartDialog by remember { mutableStateOf(false) }


    LaunchedEffect(technicianId) {
        technicianId?.let {
            viewModel.loadCurrentTechnicianEvent(it)
        }
    }

    LaunchedEffect(startEventSuccess) {
        when (startEventSuccess) {
            true -> {
                technicianId?.let { viewModel.loadCurrentTechnicianEvent(it) }
                viewModel.clearStartEventState()
            }
            false -> {
                viewModel.clearStartEventState()
            }
            null -> Unit
        }
    }

    Scaffold(
        bottomBar = {
            TechnicianNavigationBar(navController)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = "Mi trabajo actual",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (currentEvent == null) {
                Text(
                    text = "No tienes ningún evento activo.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                CurrentJobCard(
                    event = currentEvent!!,
                    onStartClick = {
                        showStartDialog = true
                    },
                    onFinishClick = {
                        navController.navigate(
                            Screen.FinalizeEvent.createRoute(currentEvent!!.idEvento)
                        )
                    }
                )
            }
        }
    }
    if (showStartDialog && currentEvent != null) {
        AlertDialog(
            onDismissRequest = {
                showStartDialog = false
            },
            title = {
                Text("Confirmar acción")
            },
            text = {
                Text("¿Deseas iniciar este trabajo?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.startEvent(currentEvent!!.idEvento)
                        showStartDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showStartDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CurrentJobCard(
    event: Event,
    onStartClick: () -> Unit,
    onFinishClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = event.tipoEvento,
                style = MaterialTheme.typography.titleLarge
            )

            Text("Estado: ${event.estadoEvento}")
            Text("Prioridad: ${event.prioridad}")
            Text("Descripción: ${event.descripcion}")

            if (event.estadoEvento == "TOMADO") {

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onStartClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar trabajo")
                }
            }

            if (event.estadoEvento == "EN PROCESO") {

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onFinishClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finalizar trabajo")
                }
            }
        }
    }
}