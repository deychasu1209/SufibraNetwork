package com.sufibra.network.ui.screens.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.ui.components.users.FormIntroCard
import com.sufibra.network.ui.components.users.RoleOptionCard
import com.sufibra.network.ui.components.users.UserSectionCard
import com.sufibra.network.viewmodel.UsersViewModel

@Composable
fun CreateUserScreen(
    navController: NavController
) {
    val viewModel: UsersViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("TECHNICIAN") }
    var telefono by remember { mutableStateOf("") }
    var zona by remember { mutableStateOf("") }

    LaunchedEffect(operationSuccess) {
        if (operationSuccess == true) {
            viewModel.resetOperationState()
            navController.popBackStack()
        }
    }

    val context = LocalContext.current

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
                title = "Crear usuario",
                navController = navController,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormIntroCard(
                    title = "Nuevo perfil de acceso",
                    subtitle = "Registra administradores o técnicos con los datos mínimos para operar dentro del sistema."
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
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Correo") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                UserSectionCard(title = "Rol del usuario") {
                    Text(
                        text = "Selecciona el perfil que tendrá acceso al sistema.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RoleOptionCard(
                            title = "Técnico",
                            subtitle = "Atiende eventos y trabajos",
                            selected = rol == "TECHNICIAN",
                            onClick = { rol = "TECHNICIAN" },
                            modifier = Modifier.weight(1f)
                        )

                        RoleOptionCard(
                            title = "Administrador",
                            subtitle = "Gestiona usuarios y eventos",
                            selected = rol == "ADMIN",
                            onClick = { rol = "ADMIN" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (rol == "TECHNICIAN") {
                    UserSectionCard(title = "Información operativa") {
                        OutlinedTextField(
                            value = telefono,
                            onValueChange = { input ->
                                telefono = input.filter { it.isDigit() }.take(9)
                            },
                            label = { Text("Teléfono") },
                            leadingIcon = {
                                Text(
                                    text = "+51 ",
                                    color = colorScheme.onSurfaceVariant
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
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

                if (errorMessage != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = errorMessage.orEmpty(),
                            modifier = Modifier.padding(16.dp),
                            color = colorScheme.onErrorContainer
                        )
                    }
                }

                Button(
                    onClick = {
                        viewModel.createUser(
                            context,
                            nombres,
                            apellidos,
                            correo,
                            password,
                            rol,
                            telefono.ifBlank { null },
                            zona.ifBlank { null }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text("Guardar usuario")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
