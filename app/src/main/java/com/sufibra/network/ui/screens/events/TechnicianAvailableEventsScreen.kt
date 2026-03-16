package com.sufibra.network.ui.screens.events

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.ui.components.navigation.TechnicianBaseScreen
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.viewmodel.EventViewModel

private const val SORT_NEWEST = "Mas recientes"
private const val SORT_OLDEST = "Mas antiguos"
private const val PRIORITY_ALL = "Todas"
private const val PRIORITY_HIGH = "Alta"
private const val PRIORITY_MEDIUM = "Media"
private const val PRIORITY_LOW = "Baja"
private const val TYPE_ALL = "Todos"
private const val TYPE_AVERIA = "Averias"
private const val TYPE_INSTALLATION = "Instalaciones"

@Composable
fun TechnicianAvailableEventsScreen(navController: NavController) {

    val viewModel: EventViewModel = viewModel()
    val events by viewModel.availableEvents.collectAsState()
    val clients by viewModel.clients.collectAsState()
    val clientsMap = clients.associateBy { it.idCliente }
    val colorScheme = MaterialTheme.colorScheme

    var filtersExpanded by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf(SORT_NEWEST) }
    var selectedPriority by remember { mutableStateOf(PRIORITY_ALL) }
    var selectedType by remember { mutableStateOf(TYPE_ALL) }

    LaunchedEffect(Unit) {
        viewModel.loadAvailableEvents()
        viewModel.loadClients()
    }

    val filteredEvents = remember(events, selectedSort, selectedPriority, selectedType) {
        events
            .filter { event ->
                val matchesPriority = when (selectedPriority) {
                    PRIORITY_HIGH -> event.prioridad.equals("ALTA", ignoreCase = true)
                    PRIORITY_MEDIUM -> event.prioridad.equals("MEDIA", ignoreCase = true)
                    PRIORITY_LOW -> event.prioridad.equals("BAJA", ignoreCase = true)
                    else -> true
                }

                val matchesType = when (selectedType) {
                    TYPE_AVERIA -> event.tipoEvento.equals("AVERIA", ignoreCase = true)
                    TYPE_INSTALLATION -> event.tipoEvento.equals("INSTALACION", ignoreCase = true)
                    else -> true
                }

                matchesPriority && matchesType
            }
            .let { list ->
                when (selectedSort) {
                    SORT_OLDEST -> list.sortedBy { it.fechaCreacion }
                    else -> list.sortedByDescending { it.fechaCreacion }
                }
            }
    }

    val activeFiltersCount = remember(selectedSort, selectedPriority, selectedType) {
        listOf(
            selectedSort != SORT_NEWEST,
            selectedPriority != PRIORITY_ALL,
            selectedType != TYPE_ALL
        ).count { it }
    }

    TechnicianBaseScreen(navController) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "Eventos Disponibles",
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { filtersExpanded = !filtersExpanded },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Filtros",
                                style = MaterialTheme.typography.titleMedium,
                                color = colorScheme.onSurface
                            )

                            if (activeFiltersCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "$activeFiltersCount activos",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (filtersExpanded) "Ocultar" else "Abrir",
                            style = MaterialTheme.typography.labelMedium,
                            color = colorScheme.primary
                        )
                    }

                    if (filtersExpanded) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CompactFilterSelector(
                                label = "Orden",
                                value = selectedSort,
                                options = listOf(SORT_NEWEST, SORT_OLDEST),
                                onOptionSelected = { selectedSort = it },
                                modifier = Modifier.weight(1f)
                            )

                            CompactFilterSelector(
                                label = "Prioridad",
                                value = selectedPriority,
                                options = listOf(PRIORITY_ALL, PRIORITY_HIGH, PRIORITY_MEDIUM, PRIORITY_LOW),
                                onOptionSelected = { selectedPriority = it },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CompactFilterSelector(
                                label = "Tipo",
                                value = selectedType,
                                options = listOf(TYPE_ALL, TYPE_AVERIA, TYPE_INSTALLATION),
                                onOptionSelected = { selectedType = it },
                                modifier = Modifier.weight(1f)
                            )

                            TextButton(
                                onClick = {
                                    selectedSort = SORT_NEWEST
                                    selectedPriority = PRIORITY_ALL
                                    selectedType = TYPE_ALL
                                }
                            ) {
                                Text("Limpiar")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${filteredEvents.size} eventos disponibles",
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredEvents) { event ->
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
                                Screen.TechnicianEventDetail.createRoute(event.idEvento)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactFilterSelector(
    label: String,
    value: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(14.dp),
            color = colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurfaceVariant
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.labelLarge,
                    color = colorScheme.onSurface
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
