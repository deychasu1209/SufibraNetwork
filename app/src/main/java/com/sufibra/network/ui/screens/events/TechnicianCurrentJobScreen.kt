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
import com.sufibra.network.domain.model.Event
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
fun TechnicianCurrentJobScreen(
    navController: NavController
) {
    val viewModel: EventViewModel = viewModel()
    val currentEvent by viewModel.currentTechnicianEvent.collectAsState()
    val client by viewModel.selectedClient.collectAsState()
    val technicianId = FirebaseAuth.getInstance().currentUser?.uid
    val startEventSuccess by viewModel.startEventSuccess.collectAsState()
    var showStartDialog by remember { mutableStateOf(false) }
    var clientExpanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

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

    LaunchedEffect(currentEvent?.idEvento) {
        clientExpanded = false
        if (currentEvent?.tipoEvento == "AVERIA" && currentEvent?.clienteId != null) {
            viewModel.loadClientForEvent(currentEvent!!.clienteId!!)
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Mi trabajo actual",
                style = MaterialTheme.typography.headlineMedium,
                color = colorScheme.onSurface
            )

            Text(
                text = "Aqui puedes revisar el evento que tienes asignado y continuar el flujo segun su estado.",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )

            if (currentEvent == null) {
                CurrentJobEmptyState()
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                val event = currentEvent!!
                val estadoColor = when (event.estadoEvento) {
                    "DISPONIBLE" -> AzulPrincipal
                    "TOMADO" -> NaranjaTomado
                    "EN PROCESO" -> Turquesa
                    "FINALIZADO" -> VerdeFinalizado
                    else -> colorScheme.outline
                }

                val prioridadColor = when (event.prioridad.uppercase()) {
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

                val colorTipo = if (event.tipoEvento.uppercase() == "AVERIA") {
                    RojoAlto
                } else {
                    AzulPrincipal
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = colorScheme.primaryContainer
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "EVENTO ACTIVO",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = if (event.estadoEvento == "EN PROCESO") {
                                "Estas trabajando en este evento ahora mismo."
                            } else {
                                "Este es el siguiente evento listo para continuar en tu flujo tecnico."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onPrimaryContainer
                        )
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
                                text = event.tipoEvento.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleMedium,
                                color = colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatusBadge(event.estadoEvento, estadoColor)
                                StatusBadge(event.prioridad.uppercase(), prioridadColor)
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
                                    Text("Telefono: ${client!!.celular}", color = colorScheme.onSurface)
                                    Text("Zona: ${client!!.zona}", color = colorScheme.onSurface)
                                    Text("Referencia: ${client!!.referencia}", color = colorScheme.onSurface)
                                    Text("Caja NAP: ${client!!.cajaNAP}", color = colorScheme.onSurface)
                                    Text("Puerto NAP: ${client!!.puertoNAP}", color = colorScheme.onSurface)

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
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                            horizontalArrangement = Arrangement.Start
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_ubicacion),
                                                contentDescription = "Ubicacion",
                                                tint = colorScheme.onPrimaryContainer
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
                                tint = colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Fecha de creacion: ${formatDate(event.fechaCreacion)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                        }

                        if (event.fechaToma != null) {
                            Text(
                                text = "Fecha de toma: ${formatDate(event.fechaToma)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                        }

                        if (event.fechaInicio != null) {
                            Text(
                                text = "Fecha de inicio: ${formatDate(event.fechaInicio)}",
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
                        Text(
                            text = "SIGUIENTE ACCION",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = when (event.estadoEvento) {
                                "TOMADO" -> "El evento ya fue tomado. Ahora puedes iniciar el trabajo cuando estes listo para comenzar."
                                "EN PROCESO" -> "El trabajo ya esta en marcha. Cuando termines, continua con la finalizacion del evento."
                                else -> "Este evento no tiene una accion manual disponible en esta pantalla."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurface
                        )

                        if (event.estadoEvento == "TOMADO") {
                            Button(
                                onClick = {
                                    showStartDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Iniciar trabajo")
                            }
                        }

                        if (event.estadoEvento == "EN PROCESO") {
                            Button(
                                onClick = {
                                    navController.navigate(
                                        Screen.FinalizeEvent.createRoute(event.idEvento)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Finalizar trabajo")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showStartDialog && currentEvent != null) {
        AlertDialog(
            onDismissRequest = {
                showStartDialog = false
            },
            title = {
                Text("Confirmar accion")
            },
            text = {
                Text("Deseas iniciar este trabajo?")
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
fun CurrentJobEmptyState() {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = colorScheme.primaryContainer,
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_tecnico),
                    contentDescription = null,
                    tint = colorScheme.onPrimaryContainer
                )
            }

            Text(
                text = "No tienes ningun evento activo",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )

            Text(
                text = "Cuando tomes un evento disponible, aparecera aqui con su detalle completo para que puedas continuarlo.",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}
