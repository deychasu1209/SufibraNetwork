package com.sufibra.network.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sufibra.network.ui.components.navigation.TechnicianBaseScreen
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.ui.components.events.TechnicianActiveJobHeroCard
import com.sufibra.network.ui.components.events.TechnicianActiveJobsEmptyCard
import com.sufibra.network.ui.components.profile.UserInitialAvatar
import com.sufibra.network.viewmodel.EventViewModel
import com.sufibra.network.viewmodel.ProfileViewModel

@Composable
fun TechnicianDashboardScreen(navController: NavController) {
    val eventViewModel: EventViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val profileViewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val currentEvent by eventViewModel.currentTechnicianEvent.collectAsState()
    val availableEvents by eventViewModel.availableEvents.collectAsState()
    val clients by eventViewModel.clients.collectAsState()
    val currentUser by profileViewModel.currentUser.collectAsState()
    val technicianId = FirebaseAuth.getInstance().currentUser?.uid
    val colorScheme = MaterialTheme.colorScheme
    val clientsMap = clients.associateBy { it.idCliente }
    val activeClient = currentEvent?.clienteId?.let { clientsMap[it] }

    LaunchedEffect(technicianId) {
        technicianId?.let {
            eventViewModel.loadCurrentTechnicianEvent(it)
            eventViewModel.loadAvailableEvents()
            eventViewModel.loadClients()
        }
    }

    LaunchedEffect(Unit) {
        profileViewModel.loadCurrentUser()
    }

    TechnicianBaseScreen(navController) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Panel Técnico",
                    style = MaterialTheme.typography.titleLarge
                )

                UserInitialAvatar(
                    initial = currentUser?.nombres?.trim()?.take(1)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = colorScheme.outlineVariant
            )

            Text(
                text = "EVENTO EN CURSO",
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (currentEvent != null) {
                TechnicianActiveJobHeroCard(
                    event = currentEvent!!,
                    clientName = activeClient?.nombresApellidos,
                    clientAddress = activeClient?.direccion,
                    onContinueClick = {
                        navController.navigate(Screen.TechnicianCurrentJob.route)
                    }
                )
            } else {
                TechnicianActiveJobsEmptyCard()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "OPERACIONES",
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModuleCard(
                title = "Eventos disponibles",
                description = "Hay ${availableEvents.size} trabajos nuevos",
                onClick = {
                    navController.navigate(Screen.TechnicianAvailableEvents.route)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ModuleCard(
                title = "Mis trabajos",
                description = "Evento activo e Historial de trabajos",
                onClick = {
                    navController.navigate(Screen.TechnicianMyJobs.route)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


