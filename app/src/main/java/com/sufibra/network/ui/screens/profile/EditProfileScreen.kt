package com.sufibra.network.ui.screens.profile

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
import com.sufibra.network.viewmodel.ProfileViewModel

@Composable
fun EditProfileScreen(
    navController: NavController
) {
    val viewModel: ProfileViewModel = viewModel()
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val profileUpdated by viewModel.profileUpdated.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    LaunchedEffect(currentUser?.idUsuario) {
        currentUser?.let {
            nombres = it.nombres
            apellidos = it.apellidos
            telefono = it.telefono.orEmpty().removePrefix("+51 ").trim()
        }
    }

    LaunchedEffect(profileUpdated) {
        if (profileUpdated == true) {
            viewModel.resetProfileUpdatedState()
            navController.popBackStack()
        }
    }

    if (currentUser == null && isLoading) {
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
                .imePadding()
                .padding(paddingValues)
        ) {
            BackTopBar(
                title = "Editar perfil",
                navController = navController
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormIntroCard(
                    title = "Actualiza tu información",
                    subtitle = "Modifica solo tus datos personales. El rol, el estado y los permisos del sistema se mantienen intactos."
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

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = currentUser?.correo.orEmpty(),
                        onValueChange = {},
                        enabled = false,
                        label = { Text("Correo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                UserSectionCard(title = "Contacto") {
                    Text(
                        text = "El teléfono es opcional, pero ayuda a mantener actualizado tu perfil dentro del sistema.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = {
                        viewModel.updateOwnProfile(
                            nombres = nombres,
                            apellidos = apellidos,
                            telefono = telefono
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && currentUser != null
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text("Guardar cambios")
                    }
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
