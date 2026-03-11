package com.sufibra.network.ui.screens.events

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.ui.components.navigation.AdminBaseScreen
import com.sufibra.network.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.graphics.Color
import com.sufibra.network.ui.theme.AzulPrincipal
import com.sufibra.network.ui.theme.NaranjaTomado
import com.sufibra.network.ui.theme.Turquesa
import com.sufibra.network.ui.theme.VerdeFinalizado
import androidx.compose.runtime.*
import androidx.compose.material3.Divider
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sufibra.network.R
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.ui.theme.AmarilloMedio
import com.sufibra.network.ui.theme.Cards
import com.sufibra.network.ui.theme.CelesteBajo
import com.sufibra.network.ui.theme.RojoAlto

@Composable
fun EventDetailScreen(
    navController: NavController,
    eventId: String
) {
    val viewModel: EventViewModel = viewModel()
    val event by viewModel.selectedEvent.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val client by viewModel.selectedClient.collectAsState()
    var clientExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val technician by viewModel.assignedTechnician.collectAsState()

    LaunchedEffect(eventId) {
        viewModel.loadEventById(eventId)
    }
    LaunchedEffect(event) {
        if (event?.tipoEvento == "AVERIA" && event?.clienteId != null) {
            viewModel.loadClientForEvent(event!!.clienteId!!)
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
                        else -> Color.Gray
                    }

                    val prioridadColor = when (e.prioridad) {
                        "ALTA" -> RojoAlto
                        "MEDIA" -> AmarilloMedio
                        "BAJA" -> CelesteBajo
                        else -> Color.Gray
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
                            containerColor = Cards
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Column (
                            modifier = Modifier.padding(16.dp)

                        ) {
                            Text(
                                text = "TIPO DE EVENTO",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
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
                                            color = Color(0xFFEFEFEF),
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
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.weight(1f)) // empuja lo siguiente a la derecha

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    StatusBadge(e.estadoEvento, estadoColor)

                                    StatusBadge(e.prioridad, prioridadColor)
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
                                containerColor = Cards
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
                                            style = MaterialTheme.typography.labelMedium
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {

                                            Icon(
                                                painter = painterResource(R.drawable.ic_persona),
                                                contentDescription = null
                                            )

                                            Spacer(modifier = Modifier.width(6.dp))

                                            Text(
                                                text = client!!.nombresApellidos,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {

                                            Icon(
                                                painter = painterResource(R.drawable.ic_ubicacion),
                                                contentDescription = null
                                            )

                                            Spacer(modifier = Modifier.width(6.dp))

                                            Text(
                                                text = client!!.direccion,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }

                                    Text(
                                        text = if (clientExpanded) "▲" else "▼"
                                    )
                                }

                                AnimatedVisibility(visible = clientExpanded) {

                                    Column(
                                        modifier = Modifier.padding(top = 16.dp)
                                    ) {

                                        Divider()

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text("DNI: ${client!!.dni}")
                                        Text("Teléfono: ${client!!.celular}")
                                        Text("Zona: ${client!!.zona}")
                                        Text("Referencia: ${client!!.referencia}")
                                        Text("Caja NAP: ${client!!.cajaNAP}")
                                        Text("Puerto NAP: ${client!!.puertoNAP}")

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = AzulPrincipal.copy(alpha = 0.08f),
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
                                                    tint = AzulPrincipal
                                                )

                                                Spacer(modifier = Modifier.width(10.dp))

                                                Text(
                                                    text = "Ver ubicación en el mapa",
                                                    color = AzulPrincipal,
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Cards
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = e.descripcion)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Cards
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
                                    contentDescription = null
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = "Técnico: ",
                                    style = MaterialTheme.typography.labelMedium
                                )

                                Text(
                                    text = technician?.let {
                                        "${it.nombres} ${it.apellidos}"
                                    } ?: "Ningún técnico ha tomado este evento",
                                    style = MaterialTheme.typography.labelMedium

                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Cards
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
                                    modifier = Modifier.size(18.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "Fecha de creación: ${formatDate(e.fechaCreacion)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Cards
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
                                color = Color.Gray
                            )
                        }
                    }

                } ?: Text("Evento no encontrado")
            }
                }
        }
    }
}
fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun StatusBadge(text: String, backgroundColor: Color) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}