package com.sufibra.network.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.sufibra.network.domain.model.Event
import com.sufibra.network.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import com.sufibra.network.ui.components.BackTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInstallationScreen(navController: NavController) {

    val viewModel: EventViewModel = viewModel()
    val scope = rememberCoroutineScope()

    var prioridad by remember { mutableStateOf("MEDIA") }
    var direccion by remember { mutableStateOf("") }
    var descripcionExtra by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {

            BackTopBar(
                title = "Nueva Instalación",
                navController = navController,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

                // PRIORIDAD
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = prioridad,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Prioridad") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("ALTA", "MEDIA", "BAJA").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    prioridad = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección referencial") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = descripcionExtra,
                    onValueChange = { descripcionExtra = it },
                    label = { Text("Datos adicionales del solicitante") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {

                        if (direccion.isBlank()) return@Button

                        isLoading = true

                        scope.launch {

                            val currentUser = FirebaseAuth.getInstance().currentUser

                            val descripcionFinal = """
                            Dirección: $direccion
                            
                            Datos adicionales:
                            $descripcionExtra
                        """.trimIndent()

                            val event = Event(
                                tipoEvento = "INSTALACION",
                                descripcion = descripcionFinal,
                                estadoEvento = "DISPONIBLE",
                                prioridad = prioridad,
                                fechaCreacion = System.currentTimeMillis(),
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
                        Text("Registrar Instalación")
                    }
                }
            }
        }
    }
}