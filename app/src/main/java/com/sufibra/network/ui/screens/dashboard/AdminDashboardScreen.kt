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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sufibra.network.R
import com.sufibra.network.domain.model.AdminDashboardMetrics
import com.sufibra.network.ui.components.navigation.AdminBaseScreen
import com.sufibra.network.ui.components.profile.UserInitialAvatar
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.ui.theme.AmarilloMedio
import com.sufibra.network.ui.theme.AzulPrincipal
import com.sufibra.network.ui.theme.NaranjaTomado
import com.sufibra.network.ui.theme.Turquesa
import com.sufibra.network.ui.theme.VerdeFinalizado
import com.sufibra.network.viewmodel.DashboardViewModel
import com.sufibra.network.viewmodel.ProfileViewModel

@Composable
fun AdminDashboardScreen(navController: NavController) {
    val viewModel: DashboardViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val metrics by viewModel.metrics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentUser by profileViewModel.currentUser.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        profileViewModel.loadCurrentUser()
    }

    AdminBaseScreen(navController) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            DashboardHeaderWithInitial(
                userInitial = currentUser?.nombres?.trim()?.take(1)
            )

            when {
                isLoading && metrics == null -> {
                    DashboardLoadingState()
                }

                errorMessage != null && metrics == null -> {
                    DashboardErrorState(
                        message = errorMessage ?: "",
                        onRetry = viewModel::loadMetrics
                    )
                }

                metrics != null -> {
                    DashboardOverviewCard(
                        metrics = metrics!!,
                        onRefresh = viewModel::loadMetrics,
                        isRefreshing = isLoading
                    )

                    DashboardSectionTitle(
                        title = "Accesos rápidos",
                        subtitle = "Entra directo a los módulos administrativos más usados."
                    )

                    QuickActionsSection(navController = navController)

                    errorMessage?.let {
                        DashboardInlineMessage(
                            message = it,
                            isError = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader() {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Panel administrador",
            style = MaterialTheme.typography.headlineSmall,
            color = colorScheme.onBackground
        )

        Text(
            text = "Consulta el estado actual del sistema y entra rápido a los módulos principales.",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DashboardHeaderWithInitial(userInitial: String?) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Panel administrador",
                style = MaterialTheme.typography.headlineSmall,
                color = colorScheme.onBackground
            )

            Text(
                text = "Consulta el estado actual del sistema y entra rápido a los módulos principales.",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.size(12.dp))
        UserInitialAvatar(initial = userInitial)
    }
}

@Composable
private fun DashboardOverviewCard(
    metrics: AdminDashboardMetrics,
    onRefresh: () -> Unit,
    isRefreshing: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Eventos registrados",
                        style = MaterialTheme.typography.labelLarge,
                        color = colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                    Text(
                        text = metrics.totalEventos.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        color = colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onRefresh,
                    enabled = !isRefreshing
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Actualizar")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryPill(
                    text = "${metrics.disponibles} disponibles",
                    background = AzulPrincipal.copy(alpha = 0.24f),
                    contentColor = colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
                SummaryPill(
                    text = "${metrics.tomados} tomados",
                    background = NaranjaTomado.copy(alpha = 0.26f),
                    contentColor = colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryPill(
                    text = "${metrics.enProceso} en proceso",
                    background = Turquesa.copy(alpha = 0.26f),
                    contentColor = colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
                SummaryPill(
                    text = "${metrics.finalizados} finalizados",
                    background = VerdeFinalizado.copy(alpha = 0.24f),
                    contentColor = colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }

            SummaryPill(
                text = "${metrics.cancelados} cancelados",
                background = AmarilloMedio.copy(alpha = 0.24f),
                contentColor = colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DashboardSectionTitle(
    title: String,
    subtitle: String
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.onSurface
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickActionsSection(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            title = "Eventos",
            description = "Gestiona programación, seguimiento y trazabilidad operativa.",
            iconRes = R.drawable.ic_eventos,
            onClick = {
                navController.navigate(Screen.EventsList.route)
            }
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCompactCard(
                title = "Usuarios",
                description = "Control de accesos y perfiles",
                iconRes = R.drawable.ic_usuarios,
                onClick = {
                    navController.navigate(Screen.UsersList.route)
                }
            )

            QuickActionCompactCard(
                title = "Clientes",
                description = "Directorio y gestión comercial",
                iconRes = R.drawable.ic_clientes,
                onClick = {
                    navController.navigate(Screen.ClientsList.route)
                }
            )
        }
    }
}

@Composable
fun DashboardLoadingState() {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Cargando métricas del sistema...",
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DashboardErrorState(
    message: String,
    onRetry: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "No se pudo cargar el panel",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun DashboardInlineMessage(
    message: String,
    isError: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    val containerColor = if (isError) colorScheme.errorContainer else colorScheme.surfaceVariant
    val contentColor = if (isError) colorScheme.onErrorContainer else colorScheme.onSurface

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.bodySmall,
            color = contentColor
        )
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    description: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = colorScheme.onPrimaryContainer
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RowScope.QuickActionCompactCard(
    title: String,
    description: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .heightIn(min = 132.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = colorScheme.surface,
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = colorScheme.primary
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SummaryPill(
    text: String,
    background: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = background
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor
        )
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
