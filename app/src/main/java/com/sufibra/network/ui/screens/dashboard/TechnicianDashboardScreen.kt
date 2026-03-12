package com.sufibra.network.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.ui.components.navigation.TechnicianBaseScreen
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.viewmodel.SessionViewModel

@Composable
fun TechnicianDashboardScreen(navController: NavController) {
    val sessionViewModel: SessionViewModel = viewModel()
    val colorScheme = MaterialTheme.colorScheme

    TechnicianBaseScreen(navController) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Panel Técnico", style = MaterialTheme.typography.titleLarge
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(colorScheme.secondaryContainer, CircleShape)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = colorScheme.outlineVariant
            )

            Text(
                text = "TAREA EN CURSO",
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            val hasActiveTask = false // 🔥 luego será dinámico

            if (hasActiveTask) {
                ActiveTaskCard()
            } else {
                NoActiveTaskCard()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "OPERACIONES",
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModuleCard(
                title = "Eventos disponibles", description = "4 tareas nuevas", onClick = {
                    navController.navigate(Screen.TechnicianAvailableEvents.route)
                }

            )

            Spacer(modifier = Modifier.height(12.dp))

            ModuleCard(
                title = "Mis trabajos", description = "Historial y agenda"
            )
            Button(
                onClick = {
                    sessionViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AdminDashboard.route) { inclusive = true }
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
fun ActiveTaskCard() {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Instalación",
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Juan Pérez Rodríguez",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Av. Principal 123, Depto 4B",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gestionar")
            }
        }
    }
}

@Composable
fun NoActiveTaskCard() {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp), contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No tienes tareas en curso",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun OperationCard(
    title: String, subtitle: String
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

