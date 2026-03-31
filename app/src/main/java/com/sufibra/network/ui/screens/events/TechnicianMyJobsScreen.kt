package com.sufibra.network.ui.screens.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sufibra.network.R
import com.sufibra.network.domain.model.Event
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.ui.components.navigation.TechnicianNavigationBar
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.ui.theme.VerdeFinalizado
import com.sufibra.network.viewmodel.EventViewModel

@Composable
fun TechnicianMyJobsScreen(
    navController: NavController
) {
    val viewModel: EventViewModel = viewModel()
    val currentEvent by viewModel.currentTechnicianEvent.collectAsState()
    val historyEvents by viewModel.technicianHistoryEvents.collectAsState()
    val clients by viewModel.clients.collectAsState()
    val technicianId = FirebaseAuth.getInstance().currentUser?.uid
    val colorScheme = MaterialTheme.colorScheme
    val clientsMap = clients.associateBy { it.idCliente }

    LaunchedEffect(technicianId) {
        technicianId?.let {
            viewModel.loadCurrentTechnicianEvent(it)
            viewModel.loadTechnicianHistoryEvents(it)
            viewModel.loadClients()
        }
    }

    val activeClient = currentEvent?.clienteId?.let { clientsMap[it] }

    Scaffold(
        containerColor = colorScheme.background,
        bottomBar = {
            TechnicianNavigationBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            BackTopBar(
                title = "Mis trabajos",
                navController = navController
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Sigue tu trabajo actual y revisa el historial de eventos finalizados.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                item {
                    JobsSectionHeader(
                        title = "ACTIVO",
                        accent = currentEvent != null
                    )
                }

                item {
                    if (currentEvent != null) {
                        TechnicianActiveJobHeroCard(
                            event = currentEvent!!,
                            clientName = activeClient?.nombresApellidos,
                            clientAddress = activeClient?.direccion,
                            onContinueClick = {
                                navController.navigate(Screen.TechnicianCurrentJob.route)
                            }
                        )
                    } else {
                        TechnicianActiveJobsEmptyCard()
                    }
                }

                item {
                    JobsSectionHeader(
                        title = "HISTORIAL",
                        subtitle = if (historyEvents.isEmpty()) "Sin eventos finalizados aún" else "${historyEvents.size} eventos finalizados",
                        accent = historyEvents.isNotEmpty()
                    )
                }

                if (historyEvents.isEmpty()) {
                    item {
                        JobsHistoryEmptyCard()
                    }
                } else {
                    items(historyEvents) { event ->
                        val client = event.clienteId?.let { clientsMap[it] }
                        HistoryJobCard(
                            event = event,
                            clientName = client?.nombresApellidos,
                            clientAddress = client?.direccion
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JobsSectionHeader(
    title: String,
    subtitle: String? = null,
    accent: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.onSurfaceVariant
            )

            if (accent) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(RoundedCornerShape(50))
                        .background(colorScheme.secondary)
                )
            }
        }

        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HistoryJobCard(
    event: Event,
    clientName: String?,
    clientAddress: String?
) {
    val colorScheme = MaterialTheme.colorScheme
    val title = if (event.tipoEvento.uppercase() == "AVERIA") {
        clientName ?: "Avería técnica"
    } else {
        "Instalación fibra"
    }
    val subtitle = if (event.tipoEvento.uppercase() == "AVERIA") {
        clientAddress ?: "Cliente sin dirección registrada"
    } else {
        extractDireccion(event.descripcion)
    }
    val dateValue = formatDate(event.fechaFinalizacion ?: event.fechaCreacion)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = VerdeFinalizado.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_trabajos),
                    contentDescription = null,
                    tint = VerdeFinalizado
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.tipoEvento.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        color = colorScheme.onSurface
                    )

                    StatusBadge("FINALIZADO", VerdeFinalizado)
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )

                Text(
                    text = dateValue,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun JobsHistoryEmptyCard() {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Aún no hay historial disponible",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )

            Text(
                text = "Los eventos que finalices aparecerán aquí como referencia de tu trabajo reciente.",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

