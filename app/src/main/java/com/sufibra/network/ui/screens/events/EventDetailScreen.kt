package com.sufibra.network.ui.screens.events

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.R
import com.sufibra.network.domain.model.Event
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.ui.components.feedback.FeedbackMessageCard
import com.sufibra.network.ui.components.feedback.FeedbackMessageType
import com.sufibra.network.ui.components.navigation.AdminBaseScreen
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.ui.theme.AmarilloMedio
import com.sufibra.network.ui.theme.AzulPrincipal
import com.sufibra.network.ui.theme.CelesteBajo
import com.sufibra.network.ui.theme.NaranjaTomado
import com.sufibra.network.ui.theme.RojoAlto
import com.sufibra.network.ui.theme.Turquesa
import com.sufibra.network.ui.theme.VerdeFinalizado
import com.sufibra.network.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EventDetailScreen(
    navController: NavController,
    eventId: String
) {
    val viewModel: EventViewModel = viewModel()
    val event by viewModel.selectedEvent.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val client by viewModel.selectedClient.collectAsState()
    val technician by viewModel.assignedTechnician.collectAsState()
    val cancelEventSuccess by viewModel.cancelEventSuccess.collectAsState()
    val releaseEventSuccess by viewModel.releaseEventSuccess.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    var showCancelDialog by remember { mutableStateOf(false) }
    var showReleaseDialog by remember { mutableStateOf(false) }
    var actionFeedbackMessage by remember { mutableStateOf<String?>(null) }
    var clientExpanded by remember { mutableStateOf(false) }
    var solutionExpanded by remember { mutableStateOf(false) }
    var observationsExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(eventId) {
        viewModel.loadEventById(eventId)
    }

    LaunchedEffect(event?.idEvento, event?.clienteId) {
        clientExpanded = false
        solutionExpanded = false
        observationsExpanded = false
        val clientId = event?.clienteId
        if (!clientId.isNullOrBlank()) viewModel.loadClientForEvent(clientId) else viewModel.clearSelectedClient()
    }

    LaunchedEffect(cancelEventSuccess) {
        if (cancelEventSuccess == true) {
            viewModel.clearCancelEventState()
            showCancelDialog = false
            actionFeedbackMessage = "El evento fue cancelado correctamente y se conservó su trazabilidad."
        }
    }

    LaunchedEffect(releaseEventSuccess) {
        if (releaseEventSuccess == true) {
            viewModel.clearReleaseEventState()
            showReleaseDialog = false
            actionFeedbackMessage = "El evento volvió a quedar disponible para la operación."
        }
    }

    AdminBaseScreen(navController) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            BackTopBar(title = "Detalle del evento", navController = navController)
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when {
                    isLoading -> CircularProgressIndicator()
                    event == null -> Text("Evento no encontrado")
                    else -> {
                        val e = event!!
                        val estadoColor = when (e.estadoEvento) {
                            "DISPONIBLE" -> AzulPrincipal
                            "TOMADO" -> NaranjaTomado
                            "EN PROCESO" -> Turquesa
                            "FINALIZADO" -> VerdeFinalizado
                            "CANCELADO" -> colorScheme.outline
                            else -> colorScheme.outline
                        }
                        val prioridadColor = when (e.prioridad) {
                            "ALTA" -> RojoAlto
                            "MEDIA" -> AmarilloMedio
                            "BAJA" -> CelesteBajo
                            else -> colorScheme.outline
                        }
                        val iconTipo = if (e.tipoEvento.uppercase() == "AVERIA") R.drawable.ic_averia else R.drawable.ic_instalacion
                        val colorTipo = if (e.tipoEvento.uppercase() == "AVERIA") RojoAlto else AzulPrincipal

                        TypeCard(e, iconTipo, colorTipo, estadoColor, prioridadColor)

                        actionFeedbackMessage?.let { FeedbackMessageCard(it, FeedbackMessageType.SUCCESS) }
                        errorMessage?.let { FeedbackMessageCard(it, FeedbackMessageType.ERROR) }

                        val canEditEvent = e.estadoEvento == "DISPONIBLE"
                        val canCancelEvent = e.estadoEvento == "DISPONIBLE" || e.estadoEvento == "TOMADO"
                        val canReleaseEvent = e.estadoEvento == "TOMADO"

                        if (canEditEvent || canCancelEvent || canReleaseEvent) {
                            AdminActionsCard(
                                canEditEvent = canEditEvent,
                                canCancelEvent = canCancelEvent,
                                canReleaseEvent = canReleaseEvent,
                                isLoading = isLoading,
                                onEdit = { navController.navigate(Screen.EditEvent.createRoute(e.idEvento)) },
                                onCancel = { showCancelDialog = true },
                                onRelease = { showReleaseDialog = true }
                            )
                        }

                        if (e.tipoEvento == "AVERIA" && client != null) {
                            ExpandableClientCard(
                                title = "Cliente",
                                name = client!!.nombresApellidos,
                                address = client!!.direccion,
                                expanded = clientExpanded,
                                onToggle = { clientExpanded = !clientExpanded },
                                extraContent = {
                                    Text("DNI: ${client!!.dni}", color = colorScheme.onSurface)
                                    Text("Teléfono: ${client!!.celular}", color = colorScheme.onSurface)
                                    Text("Zona: ${client!!.zona}", color = colorScheme.onSurface)
                                    Text("Referencia: ${client!!.referencia}", color = colorScheme.onSurface)
                                    Text("Caja NAP: ${client!!.cajaNAP}", color = colorScheme.onSurface)
                                    Text("Puerto NAP: ${client!!.puertoNAP}", color = colorScheme.onSurface)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    ClientFacadePhotoSection(photoUrl = client!!.fotoFachada, accentColor = estadoColor)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = colorScheme.primaryContainer,
                                        modifier = Modifier.fillMaxWidth().clickable {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(client!!.linkMaps)))
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(painterResource(R.drawable.ic_ubicacion), null, tint = colorScheme.onPrimaryContainer)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text("Ver ubicación en el mapa", color = colorScheme.onPrimaryContainer)
                                        }
                                    }
                                }
                            )
                        }

                        if (e.tipoEvento == "INSTALACION" && client != null) {
                            SimpleInfoCard(
                                title = "CLIENTE REGISTRADO",
                                lines = listOf(
                                    client!!.nombresApellidos,
                                    "DNI: ${client!!.dni}",
                                    client!!.direccion
                                )
                            )
                        }

                        SectionCard("DESCRIPCIÓN DEL EVENTO", e.descripcion)
                        SimpleInfoCard(
                            title = "TÉCNICO",
                            lines = listOf(technician?.let { "${it.nombres} ${it.apellidos}" } ?: "Ningún técnico ha tomado este evento")
                        )
                        DatesCard(e)

                        if (!e.solucionAplicada.isNullOrBlank()) {
                            ExpandableTextCard("SOLUCIÓN APLICADA", e.solucionAplicada, solutionExpanded) {
                                solutionExpanded = !solutionExpanded
                            }
                        }

                        if (!e.observaciones.isNullOrBlank()) {
                            ExpandableTextCard("OBSERVACIONES", e.observaciones, observationsExpanded) {
                                observationsExpanded = !observationsExpanded
                            }
                        }

                        SimpleInfoCard(title = "IDENTIFICADOR", lines = listOf("ID del evento: #${e.idEvento.takeLast(4)}"))
                    }
                }
            }
        }
    }

    if (showCancelDialog && event != null) {
        ConfirmEventDialog(
            title = "Cancelar evento",
            message = "¿Deseas cancelar este evento? El registro se conservará para trazabilidad y pasará a estado CANCELADO.",
            isLoading = isLoading,
            onConfirm = { viewModel.cancelEvent(event!!.idEvento) },
            onDismiss = { showCancelDialog = false }
        )
    }

    if (showReleaseDialog && event != null) {
        ConfirmEventDialog(
            title = "Liberar evento",
            message = "¿Deseas liberar este evento? Volverá a estado DISPONIBLE para que otro técnico pueda tomarlo.",
            isLoading = isLoading,
            onConfirm = { viewModel.releaseEvent(event!!.idEvento) },
            onDismiss = { showReleaseDialog = false }
        )
    }
}

@Composable
private fun TypeCard(
    event: Event,
    iconTipo: Int,
    colorTipo: Color,
    estadoColor: Color,
    prioridadColor: Color
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("TIPO DE EVENTO", style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(36.dp).background(colorScheme.surface, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(iconTipo), contentDescription = null, tint = colorTipo)
                }
                Text(
                    text = event.tipoEvento.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    StatusBadge(event.estadoEvento, estadoColor)
                    StatusBadge(event.prioridad, prioridadColor)
                }
            }
        }
    }
}

@Composable
private fun AdminActionsCard(
    canEditEvent: Boolean,
    canCancelEvent: Boolean,
    canReleaseEvent: Boolean,
    isLoading: Boolean,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
    onRelease: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ACCIONES ADMINISTRATIVAS", style = MaterialTheme.typography.labelMedium, color = colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (canEditEvent) {
                    "Este evento aún puede ajustarse o cancelarse antes de que avance el flujo operativo."
                } else if (canReleaseEvent) {
                    "Este evento fue tomado, pero todavía puede liberarse para que vuelva a quedar disponible sin perder trazabilidad."
                } else {
                    "Este evento ya no puede editarse, pero todavía puede cancelarse porque no inició ejecución."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (canEditEvent) {
                    Button(onClick = onEdit, modifier = Modifier.weight(1f), enabled = !isLoading) {
                        Text("Editar evento")
                    }
                }
                if (canCancelEvent) {
                    OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f), enabled = !isLoading) {
                        Text("Cancelar evento")
                    }
                }
            }
            if (canReleaseEvent) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(onClick = onRelease, modifier = Modifier.fillMaxWidth(), enabled = !isLoading) {
                    Text("Liberar evento")
                }
            }
        }
    }
}

@Composable
private fun ExpandableClientCard(
    title: String,
    name: String,
    address: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    extraContent: @Composable ColumnScope.() -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(title, style = MaterialTheme.typography.labelMedium, color = colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_persona), null, tint = colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(name, style = MaterialTheme.typography.titleMedium, color = colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_ubicacion), null, tint = colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(address, style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
                    }
                }
                Text(if (expanded) "▲" else "▼", color = colorScheme.onSurfaceVariant)
            }
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(color = colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    extraContent()
                }
            }
        }
    }
}

@Composable
fun SectionCard(title: String, body: String) {
    val colorScheme = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = colorScheme.onSurfaceVariant)
        }
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(body, color = colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun SimpleInfoCard(title: String, lines: List<String>) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = colorScheme.onSurfaceVariant)
            lines.forEachIndexed { index, line ->
                Text(
                    text = line,
                    style = if (index == 0) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodySmall,
                    color = if (index == 0) colorScheme.onSurface else colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DatesCard(event: Event) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DateRow("Fecha de creación", event.fechaCreacion)
            event.fechaToma?.let { DateRow("Fecha de toma", it) }
            event.fechaInicio?.let { DateRow("Fecha de inicio", it) }
            event.fechaFinalizacion?.let { DateRow("Fecha de finalización", it) }
            event.fechaCancelacion?.let { DateRow("Fecha de cancelación", it) }
            event.fechaLiberacion?.let { DateRow("Fecha de liberación", it) }
        }
    }
}

@Composable
private fun DateRow(label: String, timestamp: Long) {
    val colorScheme = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(R.drawable.ic_fecha),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ${formatDate(timestamp)}",
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ExpandableTextCard(
    title: String,
    text: String,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.labelMedium, color = colorScheme.onSurfaceVariant)
                Text(if (expanded) "▲" else "▼", color = colorScheme.onSurfaceVariant)
            }
            Text(
                text = if (expanded || text.length <= 140) text else "${text.take(140).trimEnd()}...",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ConfirmEventDialog(
    title: String,
    message: String,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !isLoading) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Confirmar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Volver")
            }
        }
    )
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun StatusBadge(text: String, backgroundColor: Color) {
    val colorScheme = MaterialTheme.colorScheme
    val contentColor = if (backgroundColor.luminance() > 0.75f) colorScheme.onSurface else Color.White
    Surface(color = backgroundColor, shape = RoundedCornerShape(50)) {
        Text(
            text = text,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
