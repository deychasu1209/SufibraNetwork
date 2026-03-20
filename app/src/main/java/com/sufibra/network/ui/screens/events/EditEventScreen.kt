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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.ui.theme.AmarilloMedio
import com.sufibra.network.ui.theme.AzulPrincipal
import com.sufibra.network.ui.theme.CelesteBajo
import com.sufibra.network.ui.theme.NaranjaTomado
import com.sufibra.network.ui.theme.RojoAlto
import com.sufibra.network.ui.theme.Turquesa
import com.sufibra.network.ui.theme.VerdeFinalizado
import com.sufibra.network.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    navController: NavController,
    eventId: String
) {
    val viewModel: EventViewModel = viewModel()
    val event by viewModel.selectedEvent.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val updateEventSuccess by viewModel.updateEventSuccess.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    var descripcion by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("MEDIA") }
    var priorityExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(eventId) {
        viewModel.loadEventById(eventId)
    }

    LaunchedEffect(event?.idEvento) {
        event?.let {
            descripcion = it.descripcion
            prioridad = it.prioridad
        }
    }

    LaunchedEffect(updateEventSuccess) {
        if (updateEventSuccess == true) {
            viewModel.clearUpdateEventState()
            navController.popBackStack()
        }
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
                title = "Editar evento",
                navController = navController
            )

            if (isLoading && event == null) {
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
                    event?.let { loadedEvent ->
                        val estadoColor = when (loadedEvent.estadoEvento) {
                            "DISPONIBLE" -> AzulPrincipal
                            "TOMADO" -> NaranjaTomado
                            "EN PROCESO" -> Turquesa
                            "FINALIZADO" -> VerdeFinalizado
                            "CANCELADO" -> colorScheme.outline
                            else -> colorScheme.outline
                        }

                        val prioridadColor = when (loadedEvent.prioridad) {
                            "ALTA" -> RojoAlto
                            "MEDIA" -> AmarilloMedio
                            "BAJA" -> CelesteBajo
                            else -> colorScheme.outline
                        }

                        val isEditable = loadedEvent.estadoEvento == "DISPONIBLE"
                        val eventTypeIcon =
                            if (loadedEvent.tipoEvento == "AVERIA") R.drawable.ic_averia else R.drawable.ic_instalacion

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
                                    text = "EDICIÓN ADMINISTRATIVA",
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
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = colorScheme.surface
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(id = eventTypeIcon),
                                                contentDescription = null,
                                                tint = if (loadedEvent.tipoEvento == "AVERIA") RojoAlto else AzulPrincipal,
                                                modifier = Modifier
                                                    .padding(12.dp)
                                                    .size(18.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = if (loadedEvent.tipoEvento == "AVERIA") "Avería" else "Instalación",
                                                style = MaterialTheme.typography.titleLarge,
                                                color = colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Orden #${loadedEvent.idEvento.takeLast(4)}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        StatusBadge(loadedEvent.estadoEvento, estadoColor)
                                        StatusBadge(loadedEvent.prioridad, prioridadColor)
                                    }
                                }
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Qué puedes modificar",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colorScheme.onSurface
                                )
                                Text(
                                    text = if (isEditable) {
                                        "Puedes actualizar la descripción y la prioridad mientras el evento siga en estado DISPONIBLE."
                                    } else {
                                        "Este evento ya no puede editarse porque salió del estado DISPONIBLE."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                OutlinedTextField(
                                    value = loadedEvent.tipoEvento,
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = false,
                                    label = { Text("Tipo de evento") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = descripcion,
                                    onValueChange = { descripcion = it },
                                    label = { Text("Descripción") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 4,
                                    enabled = isEditable,
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Sentences
                                    )
                                )

                                ExposedDropdownMenuBox(
                                    expanded = priorityExpanded,
                                    onExpandedChange = {
                                        if (isEditable) {
                                            priorityExpanded = !priorityExpanded
                                        }
                                    }
                                ) {
                                    OutlinedTextField(
                                        value = prioridad,
                                        onValueChange = {},
                                        readOnly = true,
                                        enabled = isEditable,
                                        label = { Text("Prioridad") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded)
                                        },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth()
                                    )

                                    DropdownMenu(
                                        expanded = priorityExpanded,
                                        onDismissRequest = { priorityExpanded = false }
                                    ) {
                                        listOf("ALTA", "MEDIA", "BAJA").forEach { option ->
                                            DropdownMenuItem(
                                                text = { Text(option) },
                                                onClick = {
                                                    prioridad = option
                                                    priorityExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        errorMessage?.let { message ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = colorScheme.errorContainer
                                )
                            ) {
                                Text(
                                    text = message,
                                    modifier = Modifier.padding(16.dp),
                                    color = colorScheme.onErrorContainer
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.updateEvent(
                                    eventId = loadedEvent.idEvento,
                                    descripcion = descripcion.trim(),
                                    prioridad = prioridad
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditable && !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator()
                            } else {
                                Text("Guardar cambios")
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
}
