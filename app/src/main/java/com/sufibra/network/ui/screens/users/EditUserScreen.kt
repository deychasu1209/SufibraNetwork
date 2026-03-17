package com.sufibra.network.ui.screens.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.ui.components.users.FormIntroCard
import com.sufibra.network.ui.components.users.UserSectionCard
import com.sufibra.network.viewmodel.UsersViewModel

@Composable
fun EditUserScreen(
    navController: NavController,
    userId: String
) {
    val viewModel: UsersViewModel = viewModel()
    val users by viewModel.users.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    val user = users.find { it.idUsuario == userId }

    if (user == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    var nombres by remember { mutableStateOf(user.nombres) }
    var apellidos by remember { mutableStateOf(user.apellidos) }
    var telefono by remember { mutableStateOf(user.telefono ?: "") }
    var zona by remember { mutableStateOf(user.zonaAsignada ?: "") }

    Scaffold(
        containerColor = colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(paddingValues)
        ) {
            BackTopBar(
                title = "Editar usuario",
                navController = navController,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormIntroCard(
                    title = "Actualiza el perfil",
                    subtitle = "Mantén al día la información operativa del usuario sin modificar su acceso técnico."
                )

                UserSectionCard(title = "Datos personales") {
                    OutlinedTextField(
                        value = nombres,
                        onValueChange = { nombres = it },
                        label = { Text("Nombres") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = apellidos,
                        onValueChange = { apellidos = it },
                        label = { Text("Apellidos") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                UserSectionCard(title = "Acceso") {
                    Text(
                        text = "Las credenciales se muestran solo como referencia y no pueden modificarse desde esta pantalla.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = user.correo,
                        onValueChange = {},
                        enabled = false,
                        label = { Text("Correo") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = "************",
                        onValueChange = {},
                        enabled = false,
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (user.rol == "TECHNICIAN") {
                    UserSectionCard(title = "Información operativa") {
                        OutlinedTextField(
                            value = telefono,
                            onValueChange = { telefono = it },
                            label = { Text("Teléfono") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = zona,
                            onValueChange = { zona = it },
                            label = { Text("Zona asignada") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Button(
                    onClick = {
                        viewModel.updateUser(
                            user.copy(
                                nombres = nombres,
                                apellidos = apellidos,
                                telefono = telefono.ifBlank { null },
                                zonaAsignada = zona.ifBlank { null }
                            )
                        )
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar cambios")
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
