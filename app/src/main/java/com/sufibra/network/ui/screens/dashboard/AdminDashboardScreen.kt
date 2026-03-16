package com.sufibra.network.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.ui.components.navigation.AdminBaseScreen
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.viewmodel.SessionViewModel

@Composable
fun AdminDashboardScreen(navController: NavController) {
    val sessionViewModel: SessionViewModel = viewModel()
    val colorScheme = MaterialTheme.colorScheme

    AdminBaseScreen(navController) { padding ->

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
                    text = "Panel Administrador",
                    style = MaterialTheme.typography.titleLarge
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            colorScheme.secondaryContainer,
                            shape = CircleShape
                        )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            StatsSection()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "MÓDULOS PRINCIPALES",
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant
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
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
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
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
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
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurface
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
    val colorScheme = MaterialTheme.colorScheme

    Card(
        onClick = { onClick?.invoke() },
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
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}




