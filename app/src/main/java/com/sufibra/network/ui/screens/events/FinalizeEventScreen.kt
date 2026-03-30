package com.sufibra.network.ui.screens.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.R
import com.sufibra.network.domain.model.Client
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.ui.components.clients.ClientForm
import com.sufibra.network.ui.components.feedback.FeedbackMessageCard
import com.sufibra.network.ui.components.feedback.FeedbackMessageType
import com.sufibra.network.ui.theme.AmarilloMedio
import com.sufibra.network.ui.theme.AzulPrincipal
import com.sufibra.network.ui.theme.CelesteBajo
import com.sufibra.network.ui.theme.NaranjaTomado
import com.sufibra.network.ui.theme.RojoAlto
import com.sufibra.network.ui.theme.Turquesa
import com.sufibra.network.ui.theme.VerdeFinalizado
import com.sufibra.network.viewmodel.EventViewModel

@Composable
fun FinalizeEventScreen(
    navController: NavController,
    eventId: String
) {
    val viewModel: EventViewModel = viewModel()
    val event by viewModel.selectedEvent.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val finalizeEventSuccess by viewModel.finalizeEventSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    var solucionAplicada by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var showClientStep by remember { mutableStateOf(false) }
    var showFinalizeDialog by remember { mutableStateOf(false) }
    var localFormError by remember { mutableStateOf<String?>(null) }

    var nombresApellidos by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var referencia by remember { mutableStateOf("") }
    var zona by remember { mutableStateOf("") }
    var cajaNap by remember { mutableStateOf("") }
    var puertoNap by remember { mutableStateOf("") }
    var linkMaps by remember { mutableStateOf("") }
    var fotoFachada by remember { mutableStateOf("") }

    LaunchedEffect(eventId) {
        viewModel.loadEventById(eventId)
    }

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

    val currentEvent = event
    val isInstallation = currentEvent?.tipoEvento == "INSTALACION"
    val buttonColor = if (showClientStep || !isInstallation) VerdeFinalizado else colorScheme.primary
    val buttonText = when {
        isInstallation && !showClientStep -> "Continuar con cliente"
        isInstallation -> "Registrar cliente y finalizar"
        else -> "Finalizar evento"
    }

    Scaffold(
        containerColor = colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BackTopBar(
                title = "Cierre del evento",
                navController = navController
            )

            if (isLoading && currentEvent == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    currentEvent?.let { loadedEvent ->
                        val estadoColor = when (loadedEvent.estadoEvento) {
                            "DISPONIBLE" -> AzulPrincipal
                            "TOMADO" -> NaranjaTomado
                            "EN PROCESO" -> Turquesa
                            "FINALIZADO" -> VerdeFinalizado
                            else -> colorScheme.outline
                        }

                        val prioridadColor = when (loadedEvent.prioridad) {
                            "ALTA" -> RojoAlto
                            "MEDIA" -> AmarilloMedio
                            "BAJA" -> CelesteBajo
                            else -> colorScheme.outline
                        }

                        FinalizeEventHeroCard(
                            eventType = loadedEvent.tipoEvento,
                            eventId = loadedEvent.idEvento,
                            estado = loadedEvent.estadoEvento,
                            prioridad = loadedEvent.prioridad,
                            estadoColor = estadoColor,
                            prioridadColor = prioridadColor
                        )

                        FinalizeEventStepCard(
                            isInstallation = isInstallation,
                            showClientStep = showClientStep
                        )

                        FinalizeSectionCard(
                            title = "Datos del cierre",
                            subtitle = if (isInstallation) {
                                "Documenta el trabajo realizado antes de registrar obligatoriamente al nuevo cliente."
                            } else {
                                "Describe la solución aplicada y cualquier observación relevante del servicio."
                            }
                        ) {
                            OutlinedTextField(
                                value = solucionAplicada,
                                onValueChange = {
                                    solucionAplicada = it
                                    if (it.isNotBlank()) localFormError = null
                                    if (errorMessage != null) viewModel.clearError()
                                },
                                label = { Text("Solución aplicada") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = observaciones,
                                onValueChange = {
                                    observaciones = it
                                    if (errorMessage != null) viewModel.clearError()
                                },
                                label = { Text("Observaciones (opcional)") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences
                                )
                            )
                        }

                        if (isInstallation && showClientStep) {
                            FinalizeSectionCard(
                                title = "Registro obligatorio del cliente",
                                subtitle = "Completa el alta del cliente para terminar correctamente la instalación."
                            ) {
                                ClientForm(
                                    nombresApellidos = nombresApellidos,
                                    dni = dni,
                                    celular = celular,
                                    direccion = direccion,
                                    referencia = referencia,
                                    zona = zona,
                                    cajaNap = cajaNap,
                                    puertoNap = puertoNap,
                                    linkMaps = linkMaps,
                                    fotoFachada = fotoFachada,
                                    onNombresApellidosChange = {
                                        nombresApellidos = it
                                        if (errorMessage != null) viewModel.clearError()
                                    },
                                    onDniChange = {
                                        dni = it
                                        if (errorMessage != null) viewModel.clearError()
                                    },
                                    onCelularChange = {
                                        celular = it
                                        if (errorMessage != null) viewModel.clearError()
                                    },
                                    onDireccionChange = {
                                        direccion = it
                                        if (errorMessage != null) viewModel.clearError()
                                    },
                                    onReferenciaChange = {
                                        referencia = it
                                        if (errorMessage != null) viewModel.clearError()
                                    },
                                    onZonaChange = {
                                        zona = it
                                        if (errorMessage != null) viewModel.clearError()
                                    },
                                    onCajaNapChange = {
                                        cajaNap = it
                                        if (errorMessage != null) viewModel.clearError()
                                    },
                                    onPuertoNapChange = {
                                        puertoNap = it
                                        if (errorMessage != null) viewModel.clearError()
                                    },
                                    onLinkMapsChange = {
                                        linkMaps = it
                                        if (errorMessage != null) viewModel.clearError()
                                    },
                                    onFotoFachadaChange = {
                                        fotoFachada = it
                                        if (errorMessage != null) viewModel.clearError()
                                    }
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = { showClientStep = false },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Volver a datos del cierre")
                                }
                            }
                        }

                        localFormError?.let { message ->
                            FeedbackMessageCard(
                                message = message,
                                type = FeedbackMessageType.ERROR
                            )
                        }

                        if (isLoading) {
                            FeedbackMessageCard(
                                message = if (isInstallation && showClientStep) {
                                    "Estamos registrando el cliente y cerrando la instalación."
                                } else {
                                    "Estamos procesando el cierre del evento."
                                },
                                type = FeedbackMessageType.INFO
                            )
                        }

                        errorMessage?.let { message ->
                            FeedbackMessageCard(
                                message = message,
                                type = FeedbackMessageType.ERROR
                            )
                        }

                        Button(
                            onClick = {
                                if (solucionAplicada.isBlank()) {
                                    localFormError = "La solución aplicada es obligatoria"
                                } else if (isInstallation && !showClientStep) {
                                    localFormError = null
                                    showClientStep = true
                                } else {
                                    localFormError = null
                                    showFinalizeDialog = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor
                            ),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(buttonText)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    } ?: Card(
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "No se pudo cargar el evento.",
                            modifier = Modifier.padding(16.dp),
                            color = colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    if (showFinalizeDialog && currentEvent != null) {
        val isInstallationConfirmation = currentEvent.tipoEvento == "INSTALACION"

        AlertDialog(
            onDismissRequest = {
                if (!isLoading) {
                    showFinalizeDialog = false
                }
            },
            title = {
                Text(if (isInstallationConfirmation) "Confirmar cierre e instalación" else "Confirmar cierre")
            },
            text = {
                Text(
                    if (isInstallationConfirmation) {
                        "Se registrará el cliente y luego se finalizará la instalación. ¿Deseas continuar?"
                    } else {
                        "¿Deseas finalizar esta avería con la información registrada?"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (isInstallationConfirmation) {
                            viewModel.finalizeInstallationWithClient(
                                eventId = eventId,
                                solucionAplicada = solucionAplicada,
                                observaciones = observaciones.ifBlank { null },
                                client = Client(
                                    nombresApellidos = nombresApellidos.trim(),
                                    dni = dni.trim(),
                                    celular = celular.trim(),
                                    direccion = direccion.trim(),
                                    referencia = referencia.trim(),
                                    zona = zona.trim(),
                                    cajaNAP = cajaNap.trim(),
                                    puertoNAP = puertoNap.trim(),
                                    linkMaps = linkMaps.trim(),
                                    fotoFachada = fotoFachada.trim(),
                                    estadoCliente = true
                                )
                            )
                        } else {
                            viewModel.finalizeEvent(
                                eventId = eventId,
                                solucionAplicada = solucionAplicada,
                                observaciones = observaciones.ifBlank { null }
                            )
                        }
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Confirmar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showFinalizeDialog = false
                    },
                    enabled = !isLoading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun FinalizeEventHeroCard(
    eventType: String,
    eventId: String,
    estado: String,
    prioridad: String,
    estadoColor: androidx.compose.ui.graphics.Color,
    prioridadColor: androidx.compose.ui.graphics.Color
) {
    val colorScheme = MaterialTheme.colorScheme
    val iconType = if (eventType == "AVERIA") R.drawable.ic_averia else R.drawable.ic_instalacion
    val accentColor = if (eventType == "AVERIA") RojoAlto else AzulPrincipal

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "CIERRE TÉCNICO",
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = accentColor.copy(alpha = 0.14f)
                    ) {
                        Icon(
                            painter = painterResource(id = iconType),
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (eventType == "AVERIA") "Avería" else "Instalación",
                            style = MaterialTheme.typography.titleLarge,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = "Orden #${eventId.takeLast(4)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatusBadge(estado, estadoColor)
                    StatusBadge(prioridad, prioridadColor)
                }
            }
        }
    }
}

@Composable
private fun FinalizeEventStepCard(
    isInstallation: Boolean,
    showClientStep: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = when {
                    !isInstallation -> "Cierre directo de avería"
                    showClientStep -> "Paso 2 de 2 · Registro de cliente"
                    else -> "Paso 1 de 2 · Datos del cierre"
                },
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onPrimaryContainer
            )

            Text(
                text = when {
                    !isInstallation -> "Registra la solución aplicada y finaliza el evento."
                    showClientStep -> "La instalación no se cerrará hasta que el cliente quede registrado correctamente."
                    else -> "Primero documenta la instalación y luego continúa al registro obligatorio del cliente."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun FinalizeSectionCard(
    title: String,
    subtitle: String,
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

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}
