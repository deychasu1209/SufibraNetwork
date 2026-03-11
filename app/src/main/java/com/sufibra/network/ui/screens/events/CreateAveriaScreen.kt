package com.sufibra.network.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.sufibra.network.domain.model.Client
import com.sufibra.network.domain.model.Event
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.viewmodel.EventViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAveriaScreen(navController: NavController) {

    val viewModel: EventViewModel = viewModel()
    val clients by viewModel.clients.collectAsState()
    val scope = rememberCoroutineScope()

    var prioridad by remember { mutableStateOf("MEDIA") }
    var descripcion by remember { mutableStateOf("") }
    var selectedClient by remember { mutableStateOf<Client?>(null) }

    var expandedPriority by remember { mutableStateOf(false) }
    var expandedClient by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadClients()
    }

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {

            BackTopBar(
                title = "Nueva Avería",
                navController = navController,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

                // PRIORIDAD
                ExposedDropdownMenuBox(
                    expanded = expandedPriority,
                    onExpandedChange = { expandedPriority = !expandedPriority }
                ) {
                    OutlinedTextField(
                        value = prioridad,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Prioridad") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedPriority,
                        onDismissRequest = { expandedPriority = false }
                    ) {
                        listOf("ALTA", "MEDIA", "BAJA").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    prioridad = option
                                    expandedPriority = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // CLIENTE
                ExposedDropdownMenuBox(
                    expanded = expandedClient,
                    onExpandedChange = { expandedClient = !expandedClient }
                ) {
                    OutlinedTextField(
                        value = selectedClient?.nombresApellidos ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seleccionar Cliente") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedClient)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedClient,
                        onDismissRequest = { expandedClient = false }
                    ) {
                        clients.forEach { client ->
                            DropdownMenuItem(
                                text = { Text(client.nombresApellidos) },
                                onClick = {
                                    selectedClient = client
                                    expandedClient = false
                                }
                            )
                        }
                    }
                }

                if (selectedClient != null) {

                    Spacer(modifier = Modifier.height(16.dp))

                    ClientDetailCard(client = selectedClient!!)

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción de la avería") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {

                        if (selectedClient == null || descripcion.isBlank()) return@Button

                        isLoading = true

                        scope.launch {

                            val currentUser = FirebaseAuth.getInstance().currentUser

                            val event = Event(
                                tipoEvento = "AVERIA",
                                descripcion = descripcion,
                                estadoEvento = "DISPONIBLE",
                                prioridad = prioridad,
                                fechaCreacion = System.currentTimeMillis(),
                                clienteId = selectedClient?.idCliente,
                                administradorId = currentUser?.uid
                            )

                            viewModel.createEvent(event)

                            isLoading = false
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Registrar Avería")
                    }
                }
            }
        }
    }
}

@Composable
fun ClientDetailCard(client: Client) {

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text = "Datos del Cliente",
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Nombre: ${client.nombresApellidos}")
            Text("DNI: ${client.dni}")
            Text("Celular: ${client.celular}")
            Text("Dirección: ${client.direccion}")
            Text("Zona: ${client.zona}")
            Text("Estado: ${if (client.estadoCliente) "Activo" else "Inactivo"}")
        }
    }
}