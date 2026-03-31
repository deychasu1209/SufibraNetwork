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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.R
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.ui.components.navigation.TechnicianNavigationBar
import com.sufibra.network.ui.theme.AmarilloMedio
import com.sufibra.network.ui.theme.AzulPrincipal
import com.sufibra.network.ui.theme.CelesteBajo
import com.sufibra.network.ui.theme.NaranjaTomado
import com.sufibra.network.ui.theme.RojoAlto
import com.sufibra.network.ui.theme.Turquesa
import com.sufibra.network.ui.theme.VerdeFinalizado
import com.sufibra.network.viewmodel.EventViewModel

@Composable
fun TechnicianHistoryEventDetailScreen(
    navController: NavController,
    eventId: String
) {
    val viewModel: EventViewModel = viewModel()
    val event by viewModel.selectedEvent.collectAsState()
    val client by viewModel.selectedClient.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val context = androidx.compose.ui.platform.LocalContext.current

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
        if (!clientId.isNullOrBlank()) {
            viewModel.loadClientForEvent(clientId)
        } else {
            viewModel.clearSelectedClient()
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        bottomBar = { TechnicianNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BackTopBar(
                title = "Detalle del trabajo",
                navController = navController
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (event == null) {
                    Text(
                        text = "No se encontró el trabajo seleccionado.",
                        color = colorScheme.onSurfaceVariant
                    )
                } else {
                    val currentEvent = event!!
                    val estadoColor = when (currentEvent.estadoEvento) {
                        "DISPONIBLE" -> AzulPrincipal
                        "TOMADO" -> NaranjaTomado
                        "EN PROCESO" -> Turquesa
                        "FINALIZADO" -> VerdeFinalizado
                        "CANCELADO" -> colorScheme.outline
                        else -> colorScheme.outline
                    }
                    val prioridadColor = when (currentEvent.prioridad) {
                        "ALTA" -> RojoAlto
                        "MEDIA" -> AmarilloMedio
                        "BAJA" -> CelesteBajo
                        else -> colorScheme.outline
                    }
                    val iconTipo = if (currentEvent.tipoEvento.uppercase() == "AVERIA") {
                        R.drawable.ic_averia
                    } else {
                        R.drawable.ic_instalacion
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "TRABAJO FINALIZADO",
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
                                    text = currentEvent.tipoEvento.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    StatusBadge(currentEvent.estadoEvento, estadoColor)
                                    StatusBadge(currentEvent.prioridad, prioridadColor)
                                }
                            }
                        }
                    }

                    if (client != null) {
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
                            Column(modifier = Modifier.padding(16.dp)) {
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
                                    Column(modifier = Modifier.padding(top = 16.dp)) {
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
                                                    context.startActivity(
                                                        Intent(Intent.ACTION_VIEW, Uri.parse(client!!.linkMaps))
                                                    )
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.ic_ubicacion),
                                                    contentDescription = null,
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

                    SectionCard(
                        title = "DESCRIPCIÓN DEL EVENTO",
                        body = currentEvent.descripcion
                    )

                    DatesCard(currentEvent)

                    currentEvent.solucionAplicada?.takeIf { it.isNotBlank() }?.let { solution ->
                        ExpandableTextCard(
                            title = "SOLUCIÓN APLICADA",
                            text = solution,
                            expanded = solutionExpanded,
                            onToggle = { solutionExpanded = !solutionExpanded }
                        )
                    }

                    currentEvent.observaciones?.takeIf { it.isNotBlank() }?.let { observations ->
                        ExpandableTextCard(
                            title = "OBSERVACIONES",
                            text = observations,
                            expanded = observationsExpanded,
                            onToggle = { observationsExpanded = !observationsExpanded }
                        )
                    }

                    SimpleInfoCard(
                        title = "IDENTIFICADOR",
                        lines = listOf("ID del evento: #${currentEvent.idEvento.takeLast(4)}")
                    )
                }
            }
        }
    }
}
