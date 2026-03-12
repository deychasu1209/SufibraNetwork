package com.sufibra.network.ui.screens.events

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
    val colorScheme = MaterialTheme.colorScheme

    var prioridad by remember { mutableStateOf("MEDIA") }
    var descripcion by remember { mutableStateOf("") }
    var selectedClient by remember { mutableStateOf<Client?>(null) }

    var expandedPriority by remember { mutableStateOf(false) }
    var expandedClient by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadClients()
    }

    Scaffold(
        containerColor = colorScheme.background
    ) { paddingValues ->

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
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text = "Datos del Cliente",
                style = MaterialTheme.typography.titleSmall,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Nombre: ${client.nombresApellidos}", color = colorScheme.onSurface)
            Text("DNI: ${client.dni}", color = colorScheme.onSurface)
            Text("Celular: ${client.celular}", color = colorScheme.onSurface)
            Text("Dirección: ${client.direccion}", color = colorScheme.onSurface)
            Text("Zona: ${client.zona}", color = colorScheme.onSurface)
            Text(
                text = "Estado: ${if (client.estadoCliente) "Activo" else "Inactivo"}",
                color = if (client.estadoCliente) colorScheme.primary else colorScheme.error
            )
        }
    }
}

