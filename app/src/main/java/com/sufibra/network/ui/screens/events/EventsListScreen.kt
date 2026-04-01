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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
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
import com.sufibra.network.viewmodel.UsersViewModel
import java.util.Calendar

private const val FILTER_ALL = "ALL"
private const val FILTER_AVAILABLE = "DISPONIBLE"
private const val FILTER_TAKEN = "TOMADO"
private const val FILTER_IN_PROGRESS = "EN PROCESO"
private const val FILTER_FINISHED = "FINALIZADO"
private const val FILTER_CANCELED = "CANCELADO"
private const val FILTER_TYPE_AVERIA = "AVERIA"
private const val FILTER_TYPE_INSTALLATION = "INSTALACION"
private const val FILTER_PRIORITY_HIGH = "ALTA"
private const val FILTER_PRIORITY_MEDIUM = "MEDIA"
private const val FILTER_PRIORITY_LOW = "BAJA"
private const val FILTER_DATE_TODAY = "TODAY"
private const val FILTER_DATE_WEEK = "WEEK"
private const val FILTER_DATE_MONTH = "MONTH"

private data class FilterOption(
    val value: String,
    val label: String
)

@Composable
fun EventsListScreen(navController: NavController) {
    val eventViewModel: EventViewModel = viewModel()
    val usersViewModel: UsersViewModel = viewModel()
    val events by eventViewModel.events.collectAsState()
    val users by usersViewModel.users.collectAsState()
    val clients by eventViewModel.clients.collectAsState()
    val clientsMap = clients.associateBy { it.idCliente }
    val techniciansMap = remember(users) {
        users.associateBy { it.idUsuario }
    }
    val colorScheme = MaterialTheme.colorScheme

    var showCreateMenu by remember { mutableStateOf(false) }
    var filtersExpanded by remember { mutableStateOf(false) }
    var advancedFiltersExpanded by remember { mutableStateOf(false) }
    var selectedState by remember { mutableStateOf(FILTER_ALL) }
    var selectedType by remember { mutableStateOf(FILTER_ALL) }
    var selectedPriority by remember { mutableStateOf(FILTER_ALL) }
    var selectedTechnician by remember { mutableStateOf(FILTER_ALL) }
    var selectedDate by remember { mutableStateOf(FILTER_ALL) }

    LaunchedEffect(Unit) {
        eventViewModel.loadClients()
        usersViewModel.loadUsers()
    }

    LaunchedEffect(
        selectedState,
        selectedType,
        selectedPriority,
        selectedTechnician
    ) {
        eventViewModel.loadEvents(
            stateFilter = selectedState.takeUnless { it == FILTER_ALL },
            typeFilter = selectedType.takeUnless { it == FILTER_ALL },
            priorityFilter = selectedPriority.takeUnless { it == FILTER_ALL },
            technicianIdFilter = selectedTechnician.takeUnless { it == FILTER_ALL }
        )
    }

    val stateOptions = remember {
        listOf(
            FilterOption(FILTER_ALL, "Todos"),
            FilterOption(FILTER_AVAILABLE, "Disponible"),
            FilterOption(FILTER_TAKEN, "Tomado"),
            FilterOption(FILTER_IN_PROGRESS, "En proceso"),
            FilterOption(FILTER_FINISHED, "Finalizado"),
            FilterOption(FILTER_CANCELED, "Cancelado")
        )
    }

    val typeOptions = remember {
        listOf(
            FilterOption(FILTER_ALL, "Todos"),
            FilterOption(FILTER_TYPE_AVERIA, "Averías"),
            FilterOption(FILTER_TYPE_INSTALLATION, "Instalaciones")
        )
    }

    val priorityOptions = remember {
        listOf(
            FilterOption(FILTER_ALL, "Todas"),
            FilterOption(FILTER_PRIORITY_HIGH, "Alta"),
            FilterOption(FILTER_PRIORITY_MEDIUM, "Media"),
            FilterOption(FILTER_PRIORITY_LOW, "Baja")
        )
    }

    val dateOptions = remember {
        listOf(
            FilterOption(FILTER_ALL, "Todas"),
            FilterOption(FILTER_DATE_TODAY, "Hoy"),
            FilterOption(FILTER_DATE_WEEK, "Esta semana"),
            FilterOption(FILTER_DATE_MONTH, "Este mes")
        )
    }

    val technicianOptions = remember(users) {
        buildList {
            add(FilterOption(FILTER_ALL, "Todos"))
            users
                .filter { it.rol == "TECHNICIAN" }
                .sortedBy { "${it.nombres} ${it.apellidos}".trim().lowercase() }
                .forEach { technician ->
                    add(
                        FilterOption(
                            value = technician.idUsuario,
                            label = "${technician.nombres} ${technician.apellidos}".trim()
                        )
                    )
                }
        }
    }

    val filteredEvents = remember(
        events,
        selectedDate
    ) {
        events
            .filter { event ->
                val matchesDate = matchesDateFilter(event.fechaCreacion, selectedDate)

                matchesDate
            }
            .sortedByDescending { it.fechaCreacion }
    }

    val activeFiltersCount = remember(
        selectedState,
        selectedType,
        selectedPriority,
        selectedTechnician,
        selectedDate
    ) {
        listOf(
            selectedState != FILTER_ALL,
            selectedType != FILTER_ALL,
            selectedPriority != FILTER_ALL,
            selectedTechnician != FILTER_ALL,
            selectedDate != FILTER_ALL
        ).count { it }
    }

    AdminBaseScreen(
        navController = navController,
        floatingActionButton = {
            Box {
                FloatingActionButton(
                    onClick = { showCreateMenu = true },
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mas),
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = showCreateMenu,
                    onDismissRequest = { showCreateMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Crear Instalación") },
                        onClick = {
                            showCreateMenu = false
                            navController.navigate(Screen.CreateInstallation.route)
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Crear Avería") },
                        onClick = {
                            showCreateMenu = false
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (filtersExpanded) {
                                        filtersExpanded = false
                                        advancedFiltersExpanded = false
                                    } else {
                                        filtersExpanded = true
                                    }
                                },
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
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }

                            Text(
                                text = if (filtersExpanded) "Ocultar" else "Abrir",
                                style = MaterialTheme.typography.labelLarge,
                                color = colorScheme.primary
                            )
                        }

                        if (filtersExpanded) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AdminEventFilterSelector(
                                    label = "Estado",
                                    selectedValue = selectedState,
                                    options = stateOptions,
                                    onOptionSelected = { selectedState = it },
                                    compact = true,
                                    modifier = Modifier.weight(1f)
                                )

                                AdminEventFilterSelector(
                                    label = "Tipo",
                                    selectedValue = selectedType,
                                    options = typeOptions,
                                    onOptionSelected = { selectedType = it },
                                    compact = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AdminEventFilterSelector(
                                    label = "Prioridad",
                                    selectedValue = selectedPriority,
                                    options = priorityOptions,
                                    onOptionSelected = { selectedPriority = it },
                                    compact = true,
                                    modifier = Modifier.weight(1f)
                                )

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { advancedFiltersExpanded = !advancedFiltersExpanded },
                                    shape = RoundedCornerShape(14.dp),
                                    color = colorScheme.surface,
                                    tonalElevation = 1.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            text = "Más",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colorScheme.onSurfaceVariant
                                        )

                                        Text(
                                            text = if (advancedFiltersExpanded) "Ocultar" else "Técnico y fecha",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        if (filtersExpanded && advancedFiltersExpanded) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AdminEventFilterSelector(
                                    label = "Técnico",
                                    selectedValue = selectedTechnician,
                                    options = technicianOptions,
                                    onOptionSelected = { selectedTechnician = it },
                                    compact = true,
                                    modifier = Modifier.weight(1f)
                                )

                                AdminEventFilterSelector(
                                    label = "Fecha",
                                    selectedValue = selectedDate,
                                    options = dateOptions,
                                    onOptionSelected = { selectedDate = it },
                                    compact = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "Limpiar",
                                    modifier = Modifier
                                        .clickable {
                                            selectedState = FILTER_ALL
                                            selectedType = FILTER_ALL
                                            selectedPriority = FILTER_ALL
                                            selectedTechnician = FILTER_ALL
                                            selectedDate = FILTER_ALL
                                        }
                                        .padding(vertical = 2.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = colorScheme.primary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${filteredEvents.size} eventos visibles",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredEvents) { event ->
                        val client = event.clienteId?.let { clientsMap[it] }
                        val technician = event.tecnicoId?.let { techniciansMap[it] }
                        val assignedTechnicianName = technician?.let {
                            formatShortTechnicianName(it.nombres, it.apellidos)
                        }

                        EventCard(
                            tipo = event.tipoEvento,
                            descripcion = event.descripcion,
                            estado = event.estadoEvento,
                            prioridad = event.prioridad,
                            fecha = event.fechaCreacion,
                            idEvento = event.idEvento,
                            nombreCliente = client?.nombresApellidos,
                            direccionCliente = client?.direccion,
                            nombreTecnico = assignedTechnicianName,
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
private fun AdminEventFilterSelector(
    label: String,
    selectedValue: String,
    options: List<FilterOption>,
    onOptionSelected: (String) -> Unit,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.value == selectedValue }?.label ?: options.first().label

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
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = if (compact) 8.dp else 10.dp
                ),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurfaceVariant
                )

                Text(
                    text = selectedLabel,
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
                    text = { Text(option.label) },
                    onClick = {
                        onOptionSelected(option.value)
                        expanded = false
                    }
                )
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
    nombreTecnico: String? = null,
    leftStripeColor: Color? = null,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    val estadoColor = when (estado) {
        "DISPONIBLE" -> AzulPrincipal
        "EN PROCESO" -> Turquesa
        "TOMADO" -> NaranjaTomado
        "FINALIZADO" -> VerdeFinalizado
        "CANCELADO" -> colorScheme.outline
        else -> colorScheme.outline
    }

    val prioridadColor = when (prioridad.uppercase()) {
        "ALTA" -> RojoAlto
        "MEDIA" -> AmarilloMedio
        "BAJA" -> CelesteBajo
        else -> colorScheme.outline
    }
    val resolvedLeftStripeColor = leftStripeColor ?: estadoColor

    val iconTipo = if (tipo.uppercase() == "AVERIA") {
        R.drawable.ic_averia
    } else {
        R.drawable.ic_instalacion
    }

    val colorTipo = if (tipo.uppercase() == "AVERIA") {
        RojoAlto
    } else {
        AzulPrincipal
    }

    val footerRightText = if (
        estado != "DISPONIBLE" &&
        estado != "CANCELADO" &&
        !nombreTecnico.isNullOrBlank()
    ) {
        "Técnico: $nombreTecnico"
    } else {
        "ID: #${idEvento.takeLast(4)}"
    }

    Spacer(modifier = Modifier.height(4.dp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                        text = if (tipo.uppercase() == "AVERIA") {
                            nombreCliente ?: "Cliente"
                        } else {
                            "Nueva instalación"
                        },
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
                            text = if (tipo.uppercase() == "AVERIA") {
                                direccionCliente ?: "Dirección no disponible"
                            } else {
                                extractDireccion(descripcion)
                            },
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

                        if (
                            estado != "DISPONIBLE" &&
                            estado != "CANCELADO" &&
                            !nombreTecnico.isNullOrBlank()
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_tecnico),
                                    contentDescription = null,
                                    tint = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )

                                Text(
                                    text = nombreTecnico,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        } else {
                            Text(
                                text = footerRightText,
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
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

private fun formatShortTechnicianName(nombres: String, apellidos: String): String? {
    val firstName = nombres.trim().split(Regex("\\s+")).firstOrNull().orEmpty()
    val firstLastName = apellidos.trim().split(Regex("\\s+")).firstOrNull().orEmpty()
    return listOf(firstName, firstLastName)
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { null }
}

private fun matchesDateFilter(
    eventTimestamp: Long,
    filterValue: String
): Boolean {
    if (filterValue == FILTER_ALL) return true

    val now = Calendar.getInstance()
    val eventDate = Calendar.getInstance().apply { timeInMillis = eventTimestamp }

    return when (filterValue) {
        FILTER_DATE_TODAY -> {
            now.get(Calendar.YEAR) == eventDate.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == eventDate.get(Calendar.DAY_OF_YEAR)
        }

        FILTER_DATE_WEEK -> {
            now.get(Calendar.YEAR) == eventDate.get(Calendar.YEAR) &&
                now.get(Calendar.WEEK_OF_YEAR) == eventDate.get(Calendar.WEEK_OF_YEAR)
        }

        FILTER_DATE_MONTH -> {
            now.get(Calendar.YEAR) == eventDate.get(Calendar.YEAR) &&
                now.get(Calendar.MONTH) == eventDate.get(Calendar.MONTH)
        }

        else -> true
    }
}
