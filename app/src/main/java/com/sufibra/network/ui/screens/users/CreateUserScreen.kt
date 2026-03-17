package com.sufibra.network.ui.screens.users

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
                title = "Crear Usuario",
                navController = navController,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {

                OutlinedTextField(
                    value = nombres,
                    onValueChange = { nombres = it },
                    label = { Text("Nombres") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { apellidos = it },
                    label = { Text("Apellidos") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Rol",
                    color = colorScheme.onSurfaceVariant
                )

                Row {
                    RadioButton(
                        selected = rol == "TECHNICIAN",
                        onClick = { rol = "TECHNICIAN" }
                    )
                    Text("Técnico")

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = rol == "ADMIN",
                        onClick = { rol = "ADMIN" }
                    )
                    Text("Administrador")
                }

                if (rol == "TECHNICIAN") {

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }.take(9)
                            telefono = digits
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

                    OutlinedTextField(
                        value = zona,
                        onValueChange = { zona = it },
                        label = { Text("Zona asignada") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                        Text("Guardar Usuario")
                    }
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
