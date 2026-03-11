package com.sufibra.network.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sufibra.network.ui.navigation.Screen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sufibra.network.viewmodel.SessionViewModel
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import com.sufibra.network.ui.theme.Turquesa
import androidx.compose.ui.res.painterResource
import com.sufibra.network.R
import androidx.compose.foundation.layout.RowScope
import com.sufibra.network.ui.components.navigation.AdminBaseScreen
import com.sufibra.network.ui.theme.Cards

@Composable
fun AdminDashboardScreen(navController: NavController) {

    val sessionViewModel: SessionViewModel = viewModel()

    AdminBaseScreen(navController) { padding ->

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
                    text = "Panel Administrador",
                    style = MaterialTheme.typography.titleLarge
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Turquesa,
                            shape = CircleShape
                        )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ESTADÍSTICAS
            StatsSection()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "MÓDULOS PRINCIPALES",
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModuleCard(
                title = "Eventos",
                description = "Gestiona programación y logística",
                onClick = {
                    navController.navigate(Screen.EventsList.route)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ModuleCard(
                title = "Usuarios",
                description = "Control de accesos y perfiles",
                onClick = {
                    navController.navigate(Screen.UsersList.route)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ModuleCard(
                title = "Clientes",
                description = "Directorio de clientes y contratos"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    sessionViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AdminDashboard.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
fun StatsSection() {

    Column {

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard("TOTAL EVENTOS", "1,240")
            StatCard("DISPONIBLES", "850")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard("EN PROCESO", "140")
            StatCard("FINALIZADOS", "250")
        }
    }
}

@Composable
fun RowScope.StatCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
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
                text = title,
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun ModuleCard(
    title: String,
    description: String,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = { onClick?.invoke() },
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
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, style = MaterialTheme.typography.bodySmall)
        }
    }
}


