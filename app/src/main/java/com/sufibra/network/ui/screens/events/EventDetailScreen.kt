package com.sufibra.network.ui.screens.events

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
    var clientExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val technician by viewModel.assignedTechnician.collectAsState()
    val cancelEventSuccess by viewModel.cancelEventSuccess.collectAsState()
    val releaseEventSuccess by viewModel.releaseEventSuccess.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    var showCancelDialog by remember { mutableStateOf(false) }
    var showReleaseDialog by remember { mutableStateOf(false) }
    var actionFeedbackMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(eventId) {
        viewModel.loadEventById(eventId)
    }
    LaunchedEffect(event) {
        if (event?.tipoEvento == "AVERIA" && event?.clienteId != null) {
            viewModel.loadClientForEvent(event!!.clienteId!!)
        }
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            BackTopBar(
                title = "Detalle del Evento",
                navController = navController,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                if (isLoading) {
                    CircularProgressIndicator()
                } else {

                    event?.let { e ->

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

                        val iconTipo = if (e.tipoEvento.uppercase() == "AVERIA")
                            R.drawable.ic_averia
                        else
                            R.drawable.ic_instalacion

                        val colorTipo = if (e.tipoEvento.uppercase() == "AVERIA")
                            RojoAlto
                        else
                            AzulPrincipal

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)

                            ) {
                                Text(
                                    text = "TIPO DE EVENTO",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                color = colorScheme.surface,
                                                shape = RoundedCornerShape(8.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(iconTipo),
                                            contentDescription = null,
                                            tint = colorTipo
                                        )
                                    }

                                    Text(
                                        text = e.tipoEvento.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.titleMedium,
                                        color = colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.weight(1f))

                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {

                                        StatusBadge(e.estadoEvento, estadoColor)
                                        StatusBadge(e.prioridad, prioridadColor)
                                    }
                                }
                            }
                        }

                        val canEditEvent = e.estadoEvento == "DISPONIBLE"
                        val canCancelEvent = e.estadoEvento == "DISPONIBLE" || e.estadoEvento == "TOMADO"
                        val canReleaseEvent = e.estadoEvento == "TOMADO"

                        actionFeedbackMessage?.let { message ->
                            Spacer(modifier = Modifier.height(16.dp))
                            FeedbackMessageCard(
                                message = message,
                                type = FeedbackMessageType.SUCCESS
                            )
                        }

                        errorMessage?.let { message ->
                            Spacer(modifier = Modifier.height(16.dp))
                            FeedbackMessageCard(
                                message = message,
                                type = FeedbackMessageType.ERROR
                            )
                        }

                        if (canEditEvent || canCancelEvent || canReleaseEvent) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = colorScheme.surfaceVariant
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 4.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "ACCIONES ADMINISTRATIVAS",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = colorScheme.onSurfaceVariant
                                    )

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
                                            Button(
                                                onClick = {
                                                    navController.navigate(Screen.EditEvent.createRoute(e.idEvento))
                                                },
                                                modifier = Modifier.weight(1f),
                                                enabled = !isLoading
                                            ) {
                                                Text("Editar evento")
                                            }
                                        }

                                        if (canCancelEvent) {
                                            OutlinedButton(
                                                onClick = { showCancelDialog = true },
                                                modifier = Modifier.weight(1f),
                                                enabled = !isLoading
                                            ) {
                                                Text("Cancelar evento")
                                            }
                                        }
                                    }

                                    if (canReleaseEvent) {
                                        Spacer(modifier = Modifier.height(10.dp))

                                        OutlinedButton(
                                            onClick = { showReleaseDialog = true },
                                            modifier = Modifier.fillMaxWidth(),
                                            enabled = !isLoading
                                        ) {
                                            Text("Liberar evento")
                                        }
                                    }
                                }
                            }
                        }

                        if (e.tipoEvento == "AVERIA" && client != null) {

                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { clientExpanded = !clientExpanded },
                                colors = CardDefaults.cardColors(
                                    containerColor = colorScheme.surfaceVariant
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 4.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = "Cliente",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = colorScheme.onSurfaceVariant
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Row(verticalAlignment = Alignment.CenterVertically) {

                                                Icon(
                                                    painter = painterResource(R.drawable.ic_persona),
                                                    contentDescription = null,
                                                    tint = colorScheme.onSurfaceVariant
                                                )

                                                Spacer(modifier = Modifier.width(6.dp))

                                                Text(
                                                    text = client!!.nombresApellidos,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = colorScheme.onSurface
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Row(verticalAlignment = Alignment.CenterVertically) {

                                                Icon(
                                                    painter = painterResource(R.drawable.ic_ubicacion),
                                                    contentDescription = null,
                                                    tint = colorScheme.onSurfaceVariant
                                                )

                                                Spacer(modifier = Modifier.width(6.dp))

                                                Text(
                                                    text = client!!.direccion,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Text(
                                            text = if (clientExpanded) "▲" else "▼",
                                            color = colorScheme.onSurfaceVariant
                                        )
                                    }

                                    AnimatedVisibility(visible = clientExpanded) {

                                        Column(
                                            modifier = Modifier.padding(top = 16.dp)
                                        ) {

                                            HorizontalDivider(color = colorScheme.outlineVariant)

                                            Spacer(modifier = Modifier.height(12.dp))

                                            Text("DNI: ${client!!.dni}", color = colorScheme.onSurface)
                                            Text("Teléfono: ${client!!.celular}", color = colorScheme.onSurface)
                                            Text("Zona: ${client!!.zona}", color = colorScheme.onSurface)
                                            Text("Referencia: ${client!!.referencia}", color = colorScheme.onSurface)
                                            Text("Caja NAP: ${client!!.cajaNAP}", color = colorScheme.onSurface)
                                            Text("Puerto NAP: ${client!!.puertoNAP}", color = colorScheme.onSurface)

                                            Spacer(modifier = Modifier.height(12.dp))

                                            ClientFacadePhotoSection(
                                                photoUrl = client!!.fotoFachada,
                                                accentColor = estadoColor
                                            )

                                            Spacer(modifier = Modifier.height(16.dp))

                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = colorScheme.primaryContainer,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        val uri = Uri.parse(client!!.linkMaps)
                                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                                        context.startActivity(intent)
                                                    }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                                    horizontalArrangement = Arrangement.Start
                                                ) {

                                                    Icon(
                                                        painter = painterResource(id = R.drawable.ic_ubicacion),
                                                        contentDescription = "Ubicación",
                                                        tint = colorScheme.onPrimaryContainer
                                                    )

                                                    Spacer(modifier = Modifier.width(10.dp))

                                                    Text(
                                                        text = "Ver ubicación en el mapa",
                                                        color = colorScheme.onPrimaryContainer,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = "DESCRIPCION DEL EVENTO",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = e.descripcion,
                                    color = colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            )
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {

                                    Icon(
                                        painter = painterResource(R.drawable.ic_tecnico),
                                        contentDescription = null,
                                        tint = colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Text(
                                        text = "Técnico: ",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = technician?.let {
                                            "${it.nombres} ${it.apellidos}"
                                        } ?: "Ningún técnico ha tomado este evento",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = colorScheme.onSurface

                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Icon(
                                        painter = painterResource(R.drawable.ic_fecha),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "Fecha de creación: ${formatDate(e.fechaCreacion)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                }

                                if (e.fechaCancelacion != null) {
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_fecha),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = "Fecha de cancelación: ${formatDate(e.fechaCancelacion)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (e.fechaLiberacion != null) {
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_fecha),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = "Fecha de liberación: ${formatDate(e.fechaLiberacion)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "ID del evento: #${e.idEvento.takeLast(4)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }

                    } ?: Text("Evento no encontrado")
                }
            }
        }
    }

    if (showCancelDialog && event != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isLoading) {
                    showCancelDialog = false
                }
            },
            title = {
                Text("Cancelar evento")
            },
            text = {
                Text("¿Deseas cancelar este evento? El registro se conservará para trazabilidad y pasará a estado CANCELADO.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelEvent(event!!.idEvento)
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
                    onClick = { showCancelDialog = false }
                    ,
                    enabled = !isLoading
                ) {
                    Text("Volver")
                }
            }
        )
    }

    if (showReleaseDialog && event != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isLoading) {
                    showReleaseDialog = false
                }
            },
            title = {
                Text("Liberar evento")
            },
            text = {
                Text("¿Deseas liberar este evento? Volverá a estado DISPONIBLE para que otro técnico pueda tomarlo.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.releaseEvent(event!!.idEvento)
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
                    onClick = { showReleaseDialog = false }
                    ,
                    enabled = !isLoading
                ) {
                    Text("Volver")
                }
            }
        )
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun StatusBadge(text: String, backgroundColor: Color) {
    val colorScheme = MaterialTheme.colorScheme
    val contentColor = if (backgroundColor.luminance() > 0.75f) {
        colorScheme.onSurface
    } else {
        Color.White
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}



