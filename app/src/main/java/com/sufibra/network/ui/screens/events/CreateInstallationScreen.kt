package com.sufibra.network.ui.screens.events

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import com.sufibra.network.domain.model.Event
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.viewmodel.EventViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInstallationScreen(navController: NavController) {

    val viewModel: EventViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme

    var prioridad by remember { mutableStateOf("MEDIA") }
    var direccion by remember { mutableStateOf("") }
    var descripcionExtra by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

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
                title = "Nueva Instalación",
                navController = navController,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

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

