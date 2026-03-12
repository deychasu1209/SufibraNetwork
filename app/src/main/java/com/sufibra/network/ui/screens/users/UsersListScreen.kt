package com.sufibra.network.ui.screens.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.domain.model.User
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.ui.components.navigation.AdminBaseScreen
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.viewmodel.UsersViewModel

@Composable
fun UsersListScreen(
    navController: NavController
) {

    val viewModel: UsersViewModel = viewModel()

    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var selectedUser by remember { mutableStateOf<User?>(null) }
    var newStatus by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    AdminBaseScreen(navController) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            BackTopBar(
                title = "Gestión de Usuarios",
                navController = navController,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

                Button(
                    onClick = {
                        navController.navigate(Screen.CreateUser.route)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crear Usuario")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                }

                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }

                LazyColumn {
                    items(users) { user ->
                        UserItem(
                            user = user,
                            navController = navController,
                            onToggleStatus = { status ->
                                selectedUser = user
                                newStatus = status
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (selectedUser != null && newStatus != null) {

                    AlertDialog(
                        onDismissRequest = {
                            selectedUser = null
                            newStatus = null
                        },
                        title = {
                            Text("Confirmar cambio de estado")
                        },
                        text = {
                            Text(
                                if (newStatus == true)
                                    "¿Deseas activar este usuario?"
                                else
                                    "¿Deseas desactivar este usuario?"
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.updateUserStatus(
                                        selectedUser!!.idUsuario,
                                        newStatus!!
                                    )
                                    selectedUser = null
                                    newStatus = null
                                }
                            ) {
                                Text("Confirmar")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    selectedUser = null
                                    newStatus = null
                                }
                            ) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: User,
    navController: NavController,
    onToggleStatus: (Boolean) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(
                text = "${user.nombres} ${user.apellidos}",
                color = colorScheme.onSurface
            )

            Text(
                text = "Rol: ${user.rol}",
                color = colorScheme.onSurfaceVariant
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = if (user.estado) "Activo" else "Inactivo",
                    color = if (user.estado)
                        colorScheme.primary
                    else
                        colorScheme.error
                )

                Switch(
                    checked = user.estado,
                    onCheckedChange = { newValue ->
                        onToggleStatus(newValue)
                    }
                )
            }

            if (user.rol == "TECHNICIAN") {
                Text(
                    text = "Zona: ${user.zonaAsignada ?: "No asignada"}",
                    color = colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    navController.navigate(
                        Screen.EditUser.createRoute(user.idUsuario)
                    )
                }
            ) {
                Text("Editar")
            }
        }
    }
}
