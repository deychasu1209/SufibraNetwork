package com.sufibra.network.ui.screens.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.R
import com.sufibra.network.domain.model.User
import com.sufibra.network.ui.components.BackTopBar
import com.sufibra.network.ui.components.navigation.AdminBaseScreen
import com.sufibra.network.ui.components.users.UserCard
import com.sufibra.network.ui.components.users.UsersEmptyState
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
    val colorScheme = MaterialTheme.colorScheme

    var selectedUser by remember { mutableStateOf<User?>(null) }
    var newStatus by remember { mutableStateOf<Boolean?>(null) }
    var expandedUserId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    val activeUsers = users.count { it.estado }
    val technicians = users.count { it.rol == "TECHNICIAN" }
    val admins = users.count { it.rol == "ADMIN" }

    AdminBaseScreen(navController) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            BackTopBar(
                title = "Gestión de usuarios",
                navController = navController
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    UsersHeaderCard(
                        totalUsers = users.size,
                        activeUsers = activeUsers,
                        technicians = technicians,
                        admins = admins,
                        onCreateUser = {
                            navController.navigate(Screen.CreateUser.route)
                        }
                    )
                }

                if (isLoading) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                errorMessage?.let { message ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.errorContainer
                            ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "No se pudo completar la acción",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = colorScheme.onErrorContainer
                                )
                                Text(
                                    text = message,
                                    color = colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }

                if (!isLoading && users.isEmpty()) {
                    item {
                        UsersEmptyState(
                            title = "Aún no hay usuarios registrados",
                            message = "Crea un administrador o técnico para comenzar a gestionar accesos."
                        )
                    }
                } else if (!isLoading) {
                    items(users, key = { it.idUsuario }) { user ->
                        UserCard(
                            user = user,
                            expanded = expandedUserId == user.idUsuario,
                            onExpandedChange = {
                                expandedUserId = if (expandedUserId == user.idUsuario) null else user.idUsuario
                            },
                            onEdit = {
                                navController.navigate(Screen.EditUser.createRoute(user.idUsuario))
                            },
                            onToggleStatus = { status ->
                                selectedUser = user
                                newStatus = status
                            }
                        )
                    }
                }
            }

            if (selectedUser != null && newStatus != null) {
                val targetUser = selectedUser!!
                val enableUser = newStatus == true

                AlertDialog(
                    onDismissRequest = {
                        if (!isLoading) {
                            selectedUser = null
                            newStatus = null
                        }
                    },
                    title = {
                        Text(if (enableUser) "Activar usuario" else "Desactivar usuario")
                    },
                    text = {
                        Text(
                            if (enableUser) {
                                "¿Deseas activar a ${targetUser.nombres} ${targetUser.apellidos}?"
                            } else {
                                "¿Deseas desactivar a ${targetUser.nombres} ${targetUser.apellidos}?"
                            }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.updateUserStatus(
                                    targetUser.idUsuario,
                                    newStatus!!
                                )
                                selectedUser = null
                                newStatus = null
                            },
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Confirmar")
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                selectedUser = null
                                newStatus = null
                            },
                            enabled = !isLoading
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun UsersHeaderCard(
    totalUsers: Int,
    activeUsers: Int,
    technicians: Int,
    admins: Int,
    onCreateUser: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Equipo y accesos",
                    style = MaterialTheme.typography.titleLarge,
                    color = colorScheme.onSurface
                )

                Text(
                    text = "Administra perfiles, estado de acceso y datos operativos del personal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UsersStatChip(
                        label = "Usuarios",
                        value = totalUsers.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    UsersStatChip(
                        label = "Activos",
                        value = activeUsers.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UsersStatChip(
                        label = "Técnicos",
                        value = technicians.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    UsersStatChip(
                        label = "Admins",
                        value = admins.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Button(
                onClick = onCreateUser,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mas),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear usuario")
            }
        }
    }
}

@Composable
private fun UsersStatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}
