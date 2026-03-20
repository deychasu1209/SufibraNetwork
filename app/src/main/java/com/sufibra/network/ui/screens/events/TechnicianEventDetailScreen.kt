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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sufibra.network.R
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.ui.components.navigation.TechnicianNavigationBar
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.ui.theme.AmarilloMedio
import com.sufibra.network.ui.theme.AzulPrincipal
import com.sufibra.network.ui.theme.CelesteBajo
import com.sufibra.network.ui.theme.NaranjaTomado
import com.sufibra.network.ui.theme.RojoAlto
import com.sufibra.network.ui.theme.Turquesa
import com.sufibra.network.ui.theme.VerdeFinalizado
import com.sufibra.network.viewmodel.EventViewModel

@Composable
fun TechnicianEventDetailScreen(
    navController: NavController,
    eventId: String
) {
    val viewModel: EventViewModel = viewModel()
    val events by viewModel.availableEvents.collectAsState()
    val client by viewModel.selectedClient.collectAsState()
    var clientExpanded by remember { mutableStateOf(false) }
    var showTakeEventDialog by remember { mutableStateOf(false) }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val takeEventSuccess by viewModel.takeEventSuccess.collectAsState()
    var showRestrictionDialog by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

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
    val showTakeAction = event?.estadoEvento == "DISPONIBLE"
    val stateAccentColor = when (event?.estadoEvento) {
        "DISPONIBLE" -> AzulPrincipal
        "TOMADO" -> NaranjaTomado
        "EN PROCESO" -> Turquesa
        "FINALIZADO" -> VerdeFinalizado
        "CANCELADO" -> colorScheme.outline
        else -> colorScheme.onSurface
    }

    LaunchedEffect(event?.idEvento) {
        clientExpanded = false
        if (event?.tipoEvento == "AVERIA" && event.clienteId != null) {
            viewModel.loadClientForEvent(event.clienteId)
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        bottomBar = {
            Column {
                if (showTakeAction) {
                    TechnicianStickyActionBar(
                        buttonText = "Tomar trabajo",
                        iconResId = R.drawable.ic_tomar,
                        containerColor = NaranjaTomado,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        onClick = {
                            showTakeEventDialog = true
                        }
                    )
                }
                TechnicianNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BackTopBar(
                title = "Detalle del Evento",
                navController = navController,
                navigationIconTint = stateAccentColor,
                navigationIconContainerColor = stateAccentColor.copy(alpha = 0.14f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (event == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Evento no disponible",
                                style = MaterialTheme.typography.titleMedium,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = "Este evento ya no esta disponible para el flujo tecnico o no se encontro.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    val estadoColor = when (event.estadoEvento) {
                        "DISPONIBLE" -> AzulPrincipal
                        "TOMADO" -> NaranjaTomado
                        "EN PROCESO" -> Turquesa
                        "FINALIZADO" -> VerdeFinalizado
                        "CANCELADO" -> colorScheme.outline
                        else -> colorScheme.outline
                    }

                    val prioridadColor = when (event.prioridad) {
                        "ALTA" -> RojoAlto
                        "MEDIA" -> AmarilloMedio
                        "BAJA" -> CelesteBajo
                        else -> colorScheme.outline
                    }

                    val iconTipo = if (event.tipoEvento.uppercase() == "AVERIA") {
                        R.drawable.ic_averia
                    } else {
                        R.drawable.ic_instalacion
                    }

                    Text(
                        text = "Revisa la informacion del evento antes de tomarlo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                                        tint = estadoColor
                                    )
                                }

                                Text(
                                    text = event.tipoEvento.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    StatusBadge(event.estadoEvento, estadoColor)
                                    StatusBadge(event.prioridad, prioridadColor)
                                }
                            }
                        }
                    }

                    if (event.tipoEvento == "AVERIA" && client != null) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { clientExpanded = !clientExpanded },
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                                                tint = estadoColor
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
                                                tint = estadoColor
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
                                        Text("Telefono: ${client!!.celular}", color = colorScheme.onSurface)
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
                                                    contentDescription = "Ubicacion",
                                                    tint = estadoColor
                                                )

                                                Spacer(modifier = Modifier.width(10.dp))

                                                Text(
                                                    text = "Ver ubicacion en el mapa",
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

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = "DESCRIPCION DEL EVENTO",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = event.descripcion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_fecha),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = estadoColor
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "Fecha de creacion: ${formatDate(event.fechaCreacion)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "ID del evento: #${event.idEvento.takeLast(4)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showTakeEventDialog && event != null) {
        AlertDialog(
            onDismissRequest = {
                showTakeEventDialog = false
            },
            title = {
                Text("Confirmar accion")
            },
            text = {
                Text("Deseas tomar este evento?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val technicianId = FirebaseAuth
                            .getInstance()
                            .currentUser
                            ?.uid ?: return@TextButton

                        viewModel.takeEvent(event, technicianId)
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



