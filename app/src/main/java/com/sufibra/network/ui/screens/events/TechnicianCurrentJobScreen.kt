package com.sufibra.network.ui.screens.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
    val colorScheme = MaterialTheme.colorScheme

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
        containerColor = colorScheme.background,
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
                style = MaterialTheme.typography.headlineMedium,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (currentEvent == null) {
                Text(
                    text = "No tienes ningún evento activo.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant
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
    val colorScheme = MaterialTheme.colorScheme

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
                text = event.tipoEvento,
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurface
            )

            Text(
                text = "Estado: ${event.estadoEvento}",
                color = colorScheme.onSurfaceVariant
            )
            Text(
                text = "Prioridad: ${event.prioridad}",
                color = colorScheme.onSurfaceVariant
            )
            Text(
                text = "Descripción: ${event.descripcion}",
                color = colorScheme.onSurface
            )

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
