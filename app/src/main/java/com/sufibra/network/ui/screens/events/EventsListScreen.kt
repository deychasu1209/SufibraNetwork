package com.sufibra.network.ui.screens.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.R
import com.sufibra.network.ui.components.BackTopBar
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

@Composable
fun EventsListScreen(navController: NavController) {

    val viewModel: EventViewModel = viewModel()
    val events by viewModel.events.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    val clients by viewModel.clients.collectAsState()
    val clientsMap = clients.associateBy { it.idCliente }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.loadEvents()
        viewModel.loadClients()
    }

    AdminBaseScreen(
        navController = navController,
        floatingActionButton = {
            Box {
                FloatingActionButton(
                    onClick = { showMenu = true },
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mas),
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Crear Instalación") },
                        onClick = {
                            showMenu = false
                            navController.navigate(Screen.CreateInstallation.route)
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Crear Avería") },
                        onClick = {
                            showMenu = false
                            navController.navigate(Screen.CreateAveria.route)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            BackTopBar(
                title = "Eventos",
                navController = navController,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Buscar evento...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(events) { event ->
                        val client = event.clienteId?.let { clientsMap[it] }

                        EventCard(
                            tipo = event.tipoEvento,
                            descripcion = event.descripcion,
                            estado = event.estadoEvento,
                            prioridad = event.prioridad,
                            fecha = event.fechaCreacion,
                            idEvento = event.idEvento,
                            nombreCliente = client?.nombresApellidos,
                            direccionCliente = client?.direccion,
                            onClick = {
                                navController.navigate(
                                    Screen.EventDetail.createRoute(event.idEvento)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(
    tipo: String,
    descripcion: String,
    estado: String,
    prioridad: String,
    fecha: Long,
    idEvento: String,
    nombreCliente: String?,
    direccionCliente: String?,
    leftStripeColor: Color? = null,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    val estadoColor = when (estado) {
        "DISPONIBLE" -> AzulPrincipal
        "EN PROCESO" -> Turquesa
        "TOMADO" -> NaranjaTomado
        "FINALIZADO" -> VerdeFinalizado
        else -> colorScheme.outline
    }

    val prioridadColor = when (prioridad.uppercase()) {
        "ALTA" -> RojoAlto
        "MEDIA" -> AmarilloMedio
        "BAJA" -> CelesteBajo
        else -> colorScheme.outline
    }
    val resolvedLeftStripeColor = leftStripeColor ?: estadoColor

    val iconTipo = if (tipo.uppercase() == "AVERIA")
        R.drawable.ic_averia
    else
        R.drawable.ic_instalacion

    val colorTipo = if (tipo.uppercase() == "AVERIA")
        RojoAlto
    else
        AzulPrincipal

    Spacer(modifier = Modifier.height(4.dp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(resolvedLeftStripeColor)
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(2.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

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

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = tipo.uppercase(),
                                style = MaterialTheme.typography.labelLarge,
                                color = colorScheme.onSurface
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {

                            StatusBadge(estado, estadoColor)

                            Spacer(modifier = Modifier.height(4.dp))

                            StatusBadge(prioridad.uppercase(), prioridadColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (tipo.uppercase() == "AVERIA") nombreCliente ?: "Cliente" else "Nueva instalación",
                        style = MaterialTheme.typography.titleMedium,
                        color = colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            painter = painterResource(R.drawable.ic_ubicacion),
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = if (tipo.uppercase() == "AVERIA") direccionCliente ?: "Direccion no disponible"
                            else extractDireccion(descripcion),
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(color = colorScheme.outlineVariant)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Icon(
                                painter = painterResource(id = R.drawable.ic_fecha),
                                contentDescription = null,
                                tint = colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = formatDate(fecha),
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "ID: #${idEvento.takeLast(4)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

}

fun extractDireccion(descripcion: String): String {

    val regex = Regex("Dirección:\\s*(.*?)\\s*Datos adicionales")
    val match = regex.find(descripcion)

    return match?.groupValues?.get(1) ?: descripcion
}
