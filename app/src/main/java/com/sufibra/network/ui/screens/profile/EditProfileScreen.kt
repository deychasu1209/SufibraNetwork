package com.sufibra.network.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
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
        currentUser?.let { user ->
            nombres = user.nombres
            apellidos = user.apellidos
            telefono = user.telefono.orEmpty().removePrefix("+51 ").trim()
        }
    }

    LaunchedEffect(profileUpdated) {
        if (profileUpdated == true) {
            viewModel.resetProfileUpdatedState()
            navController.popBackStack()
        }
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
                    subtitle = "Mantén tus datos personales al día sin modificar tu rol ni la configuración operativa del sistema."
                )

                UserSectionCard(title = "Datos personales") {
                    OutlinedTextField(
                        value = nombres,
                        onValueChange = {
                            nombres = it
                            viewModel.clearError()
                        },
                        label = { Text("Nombres") },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = apellidos,
                        onValueChange = {
                            apellidos = it
                            viewModel.clearError()
                        },
                        label = { Text("Apellidos") },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                UserSectionCard(title = "Contacto") {
                    Text(
                        text = "El teléfono es opcional, pero si lo registras debe tener 9 dígitos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { input ->
                            telefono = input.filter { it.isDigit() }.take(9)
                            viewModel.clearError()
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
                }

                errorMessage?.let { message ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(16.dp),
                            color = colorScheme.onErrorContainer
                        )
                    }
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

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
