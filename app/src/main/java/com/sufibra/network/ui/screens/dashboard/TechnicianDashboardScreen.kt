package com.sufibra.network.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sufibra.network.R
import com.sufibra.network.ui.components.navigation.TechnicianBaseScreen
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.ui.theme.Cards
import com.sufibra.network.ui.theme.Turquesa
import com.sufibra.network.viewmodel.SessionViewModel


@Composable
fun TechnicianDashboardScreen(navController: NavController) {

    val sessionViewModel: SessionViewModel = viewModel()

    TechnicianBaseScreen(navController) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // HEADER
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
                        .background(Turquesa, CircleShape)
                )
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // TAREA EN CURSO
            Text(
                text = "TAREA EN CURSO", style = MaterialTheme.typography.labelMedium
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
                text = "OPERACIONES", style = MaterialTheme.typography.labelMedium
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

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Cards
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Instalación", style = MaterialTheme.typography.labelMedium, color = Turquesa
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Juan Pérez Rodríguez", style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Av. Principal 123, Depto 4B", style = MaterialTheme.typography.bodySmall
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

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Cards
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
                text = "No tienes tareas en curso", style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun OperationCard(
    title: String, subtitle: String
) {

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Cards
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
                text = title, style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle, style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
