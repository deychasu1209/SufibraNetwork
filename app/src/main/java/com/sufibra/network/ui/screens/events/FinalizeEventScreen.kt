package com.sufibra.network.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.viewmodel.EventViewModel

@Composable
fun FinalizeEventScreen(
    navController: NavController,
    eventId: String
) {

    var solucionAplicada by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    val viewModel: EventViewModel = viewModel()
    val finalizeEventSuccess by viewModel.finalizeEventSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showFinalizeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(finalizeEventSuccess) {
        when (finalizeEventSuccess) {
            true -> {
                viewModel.clearFinalizeEventState()
                navController.popBackStack()
            }
            false -> Unit
            null -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackTopBar(
            title = "Finalizar Evento",
            navController = navController
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Completa la información para finalizar el evento.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = solucionAplicada,
                onValueChange = { solucionAplicada = it },
                label = { Text("Solución aplicada") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                )
            )

            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                )
            )

            Button(
                onClick = {
                    showFinalizeDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Finalizar evento")
            }
        }
    }

    if (showFinalizeDialog) {
        AlertDialog(
            onDismissRequest = {
                showFinalizeDialog = false
            },
            title = {
                Text("Confirmar acción")
            },
            text = {
                Text("¿Deseas finalizar este evento?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.finalizeEvent(
                            eventId = eventId,
                            solucionAplicada = solucionAplicada,
                            observaciones = observaciones.ifBlank { null }
                        )
                        showFinalizeDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showFinalizeDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = {
                viewModel.clearError()
            },
            title = {
                Text("Aviso")
            },
            text = {
                Text(message)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearError()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

}