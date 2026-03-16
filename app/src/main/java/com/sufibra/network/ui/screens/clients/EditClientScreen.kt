package com.sufibra.network.ui.screens.clients

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.domain.model.Client
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.ui.components.clients.ClientForm
import com.sufibra.network.viewmodel.ClientsViewModel

@Composable
fun EditClientScreen(
    navController: NavController,
    clientId: String
) {
    val viewModel: ClientsViewModel = viewModel()
    val selectedClient by viewModel.selectedClient.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    var nombresApellidos by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var referencia by remember { mutableStateOf("") }
    var zona by remember { mutableStateOf("") }
    var cajaNap by remember { mutableStateOf("") }
    var puertoNap by remember { mutableStateOf("") }
    var linkMaps by remember { mutableStateOf("") }
    var fotoFachada by remember { mutableStateOf("") }

    LaunchedEffect(clientId) {
        viewModel.loadClientById(clientId)
    }

    LaunchedEffect(selectedClient?.idCliente) {
        selectedClient?.let { client ->
            nombresApellidos = client.nombresApellidos
            dni = client.dni
            celular = client.celular
            direccion = client.direccion
            referencia = client.referencia
            zona = client.zona
            cajaNap = client.cajaNAP
            puertoNap = client.puertoNAP
            linkMaps = client.linkMaps
            fotoFachada = client.fotoFachada
        }
    }

    LaunchedEffect(operationSuccess) {
        if (operationSuccess == true) {
            viewModel.resetOperationState()
            navController.popBackStack()
        }
    }

    if (selectedClient == null && isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
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
                title = "Editar Cliente",
                navController = navController
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                ClientForm(
                    nombresApellidos = nombresApellidos,
                    dni = dni,
                    celular = celular,
                    direccion = direccion,
                    referencia = referencia,
                    zona = zona,
                    cajaNap = cajaNap,
                    puertoNap = puertoNap,
                    linkMaps = linkMaps,
                    fotoFachada = fotoFachada,
                    onNombresApellidosChange = { nombresApellidos = it },
                    onDniChange = { dni = it },
                    onCelularChange = { celular = it },
                    onDireccionChange = { direccion = it },
                    onReferenciaChange = { referencia = it },
                    onZonaChange = { zona = it },
                    onCajaNapChange = { cajaNap = it },
                    onPuertoNapChange = { puertoNap = it },
                    onLinkMapsChange = { linkMaps = it },
                    onFotoFachadaChange = { fotoFachada = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val currentClient = selectedClient ?: return@Button

                        viewModel.updateClient(
                            Client(
                                idCliente = currentClient.idCliente,
                                nombresApellidos = nombresApellidos.trim(),
                                dni = dni.trim(),
                                celular = celular.trim(),
                                direccion = direccion.trim(),
                                referencia = referencia.trim(),
                                zona = zona.trim(),
                                cajaNAP = cajaNap.trim(),
                                puertoNAP = puertoNap.trim(),
                                linkMaps = linkMaps.trim(),
                                fotoFachada = fotoFachada.trim(),
                                estadoCliente = currentClient.estadoCliente,
                                fechaRegistro = currentClient.fechaRegistro
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && selectedClient != null
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text("Guardar Cambios")
                    }
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = it,
                        color = colorScheme.error
                    )
                }
            }
        }
    }
}
