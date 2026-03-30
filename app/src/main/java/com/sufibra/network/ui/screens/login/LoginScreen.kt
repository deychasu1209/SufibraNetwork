package com.sufibra.network.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.R
import com.sufibra.network.ui.components.feedback.FeedbackMessageCard
import com.sufibra.network.ui.components.feedback.FeedbackMessageType
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.ui.theme.AzulPrincipal
import com.sufibra.network.ui.theme.AzulPrincipalOscuro
import com.sufibra.network.ui.theme.DarkBackground
import com.sufibra.network.ui.theme.Turquesa
import com.sufibra.network.viewmodel.LoginViewModel

@Composable
fun LoginScreen(navController: NavController) {

    val viewModel: LoginViewModel = viewModel()

    val isLoading by viewModel.isLoading.collectAsState()
    val isRecoveryLoading by viewModel.isRecoveryLoading.collectAsState()
    val loggedUser by viewModel.loggedUser.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val recoveryErrorMessage by viewModel.recoveryErrorMessage.collectAsState()
    val recoverySuccessMessage by viewModel.recoverySuccessMessage.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val gradientColors = if (colorScheme.background == DarkBackground) {
        listOf(AzulPrincipalOscuro, AzulPrincipal)
    } else {
        listOf(AzulPrincipal, Turquesa)
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showRecoveryDialog by remember { mutableStateOf(false) }
    var recoveryEmail by remember { mutableStateOf("") }

    LaunchedEffect(loggedUser) {
        loggedUser?.let { user ->
            if (user.rol == "ADMIN") {
                navController.navigate(Screen.AdminDashboard.route) {
                    popUpTo(0) { inclusive = true }
                }
            } else if (user.rol == "TECHNICIAN") {
                navController.navigate(Screen.TechnicianDashboard.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            viewModel.resetLoginState()
        }
    }

    if (showRecoveryDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isRecoveryLoading) {
                    showRecoveryDialog = false
                    viewModel.clearRecoveryError()
                }
            },
            title = {
                Text("Recuperar contraseña")
            },
            text = {
                Column {
                    Text(
                        text = "Ingresa tu correo y te enviaremos un enlace para restablecer tu contraseña.",
                        color = colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = recoveryEmail,
                        onValueChange = {
                            recoveryEmail = it
                            if (recoveryErrorMessage != null) {
                                viewModel.clearRecoveryError()
                            }
                        },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isRecoveryLoading,
                        singleLine = true
                    )

                    recoveryErrorMessage?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        FeedbackMessageCard(
                            message = it,
                            type = FeedbackMessageType.ERROR
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.sendPasswordRecovery(recoveryEmail) },
                    enabled = !isRecoveryLoading
                ) {
                    if (isRecoveryLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Enviar correo")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRecoveryDialog = false
                        viewModel.clearRecoveryError()
                    },
                    enabled = !isRecoveryLoading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    recoverySuccessMessage?.let { successMessage ->
        AlertDialog(
            onDismissRequest = { viewModel.clearRecoverySuccess() },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.clearRecoverySuccess() }
                ) {
                    Text("Entendido")
                }
            },
            title = {
                Text("Correo enviado")
            },
            text = {
                Text(successMessage)
            }
        )

        LaunchedEffect(successMessage) {
            showRecoveryDialog = false
            recoveryEmail = ""
            viewModel.clearRecoveryError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = gradientColors
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_blanco),
                    contentDescription = "Logo Sufibra",
                    modifier = Modifier.size(100.dp)
                )
                Text(
                    text = "Sufibra",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "NETWORK",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )
            }

            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.titleLarge,
                        color = colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Bienvenido a tu red de fibra",
                        color = colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (errorMessage != null) viewModel.resetError()
                        },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (errorMessage != null) viewModel.resetError()
                        },
                        label = { Text("Contraseña") },
                        visualTransformation = if (showPassword) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = showPassword,
                                onCheckedChange = { showPassword = it }
                            )

                            Text(
                                text = "Mostrar contraseña",
                                color = colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .clickable { showPassword = !showPassword }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.login(email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isLoading && !isRecoveryLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = colorScheme.onPrimary
                            )
                        } else {
                            Text("Iniciar sesión")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            color = colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable {
                                if (!isLoading && !isRecoveryLoading) {
                                    recoveryEmail = email
                                    showRecoveryDialog = true
                                    viewModel.clearRecoveryError()
                                    viewModel.clearRecoverySuccess()
                                }
                            }
                        )
                    }

                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        FeedbackMessageCard(
                            message = it,
                            type = FeedbackMessageType.ERROR
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "© Sufibra Network",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp
            )
        }
    }
}
