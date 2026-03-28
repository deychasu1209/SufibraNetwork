package com.sufibra.network.ui.screens.clients

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.R
import com.sufibra.network.domain.model.Client
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.ui.components.clients.ClientCard
import com.sufibra.network.ui.components.clients.ClientsEmptyState
import com.sufibra.network.ui.components.navigation.AdminBaseScreen
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.viewmodel.ClientsViewModel
import java.text.Normalizer

private const val ORDER_NEWEST = "Más recientes"
private const val ORDER_OLDEST = "Más antiguos"
private const val ORDER_NAME_ASC = "Nombre A-Z"
private const val ORDER_NAME_DESC = "Nombre Z-A"
private const val STATUS_ALL = "Todos"
private const val STATUS_ACTIVE = "Activos"
private const val STATUS_INACTIVE = "Inactivos"
private const val ZONE_ALL = "Todas"

@Composable
fun ClientsListScreen(
    navController: NavController
) {
    val viewModel: ClientsViewModel = viewModel()
    val clients by viewModel.clients.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var expandedClientId by remember { mutableStateOf<String?>(null) }
    var clientToToggle by remember { mutableStateOf<Client?>(null) }
    var targetStatus by remember { mutableStateOf<Boolean?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var filtersExpanded by remember { mutableStateOf(false) }
    var selectedOrder by remember { mutableStateOf(ORDER_NEWEST) }
    var selectedStatus by remember { mutableStateOf(STATUS_ALL) }
    var selectedZone by remember { mutableStateOf(ZONE_ALL) }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.loadClients()
    }

    val zoneOptions = remember(clients) {
        listOf(ZONE_ALL) + clients
            .map { it.zona.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    val filteredClients = remember(clients, searchQuery, selectedOrder, selectedStatus, selectedZone) {
        clients
            .filter { client ->
                val matchesSearch = if (searchQuery.isBlank()) {
                    true
                } else {
                    val query = normalizeSearchText(searchQuery)
                    normalizeSearchText(client.nombresApellidos).contains(query) ||
                        normalizeSearchText(client.dni).contains(query) ||
                        normalizeSearchText(client.celular).contains(query) ||
                        normalizeSearchText(client.direccion).contains(query) ||
                        normalizeSearchText(client.zona).contains(query)
                }

                val matchesStatus = when (selectedStatus) {
                    STATUS_ACTIVE -> client.estadoCliente
                    STATUS_INACTIVE -> !client.estadoCliente
                    else -> true
                }

                val matchesZone = when (selectedZone) {
                    ZONE_ALL -> true
                    else -> client.zona.equals(selectedZone, ignoreCase = true)
                }

                matchesSearch && matchesStatus && matchesZone
            }
            .let { list ->
                when (selectedOrder) {
                    ORDER_OLDEST -> list.sortedBy { it.fechaRegistro }
                    ORDER_NAME_ASC -> list.sortedBy { it.nombresApellidos.lowercase() }
                    ORDER_NAME_DESC -> list.sortedByDescending { it.nombresApellidos.lowercase() }
                    else -> list.sortedByDescending { it.fechaRegistro }
                }
            }
    }

    val activeFiltersCount = remember(selectedOrder, selectedStatus, selectedZone) {
        listOf(
            selectedOrder != ORDER_NEWEST,
            selectedStatus != STATUS_ALL,
            selectedZone != ZONE_ALL
        ).count { it }
    }

    AdminBaseScreen(
        navController = navController,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateClient.route) },
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_mas),
                    contentDescription = "Crear cliente"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            BackTopBar(
                title = "Clientes",
                navController = navController
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar cliente...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

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
                            CompactClientFilterSelector(
                                label = "Orden",
                                value = selectedOrder,
                                options = listOf(ORDER_NEWEST, ORDER_OLDEST, ORDER_NAME_ASC, ORDER_NAME_DESC),
                                onOptionSelected = { selectedOrder = it },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CompactClientFilterSelector(
                                    label = "Estado",
                                    value = selectedStatus,
                                    options = listOf(STATUS_ALL, STATUS_ACTIVE, STATUS_INACTIVE),
                                    onOptionSelected = { selectedStatus = it },
                                    modifier = Modifier.weight(1f)
                                )

                                CompactClientFilterSelector(
                                    label = "Zona",
                                    value = selectedZone,
                                    options = zoneOptions,
                                    onOptionSelected = { selectedZone = it },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            TextButton(
                                onClick = {
                                    selectedOrder = ORDER_NEWEST
                                    selectedStatus = STATUS_ALL
                                    selectedZone = ZONE_ALL
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Limpiar")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                errorMessage?.let { message ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "No se pudo completar la acción",
                                style = MaterialTheme.typography.titleSmall,
                                color = colorScheme.onErrorContainer
                            )
                            Text(
                                text = message,
                                color = colorScheme.onErrorContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (clients.isEmpty()) {
                    ClientsEmptyState()
                } else if (filteredClients.isEmpty()) {
                    ClientsEmptyState(
                        title = "No se encontraron clientes",
                        description = "Prueba con otro texto o ajusta los filtros para ver más resultados."
                    )
                } else {
                    Text(
                        text = "${filteredClients.size} clientes visibles",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredClients) { client ->
                            ClientCard(
                                client = client,
                                expanded = expandedClientId == client.idCliente,
                                onExpandedChange = {
                                    expandedClientId = if (expandedClientId == client.idCliente) null else client.idCliente
                                },
                                onEdit = {
                                    navController.navigate(
                                        Screen.EditClient.createRoute(client.idCliente)
                                    )
                                },
                                onToggleStatus = { newStatus ->
                                    clientToToggle = client
                                    targetStatus = newStatus
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (clientToToggle != null && targetStatus != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isLoading) {
                    clientToToggle = null
                    targetStatus = null
                }
            },
            title = { Text("Confirmar cambio de estado") },
            text = {
                Text(
                    if (targetStatus == true) {
                        "Se activará el cliente ${clientToToggle!!.nombresApellidos}."
                    } else {
                        "Se desactivará el cliente ${clientToToggle!!.nombresApellidos}."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateClientStatus(clientToToggle!!.idCliente, targetStatus!!)
                        clientToToggle = null
                        targetStatus = null
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(if (targetStatus == true) "Activar" else "Desactivar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        clientToToggle = null
                        targetStatus = null
                    },
                    enabled = !isLoading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun normalizeSearchText(value: String): String {
    return Normalizer
        .normalize(value.trim().lowercase(), Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
}

@Composable
private fun CompactClientFilterSelector(
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
