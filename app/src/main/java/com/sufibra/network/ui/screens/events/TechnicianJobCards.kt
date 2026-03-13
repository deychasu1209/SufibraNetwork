package com.sufibra.network.ui.screens.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sufibra.network.R
import com.sufibra.network.domain.model.Event
import com.sufibra.network.ui.theme.AmarilloMedio
import com.sufibra.network.ui.theme.AzulPrincipal
import com.sufibra.network.ui.theme.AzulPrincipalOscuro
import com.sufibra.network.ui.theme.CelesteBajo
import com.sufibra.network.ui.theme.NaranjaTomado
import com.sufibra.network.ui.theme.RojoAlto
import com.sufibra.network.ui.theme.Turquesa
import com.sufibra.network.ui.theme.VerdeFinalizado

@Composable
fun TechnicianActiveJobHeroCard(
    event: Event,
    clientName: String?,
    clientAddress: String?,
    onContinueClick: () -> Unit
) {
    val estadoColor = when (event.estadoEvento) {
        "DISPONIBLE" -> AzulPrincipal
        "TOMADO" -> NaranjaTomado
        "EN PROCESO" -> Turquesa
        "FINALIZADO" -> VerdeFinalizado
        else -> Color.White.copy(alpha = 0.30f)
    }

    val prioridadColor = when (event.prioridad.uppercase()) {
        "ALTA" -> RojoAlto
        "MEDIA" -> AmarilloMedio
        "BAJA" -> CelesteBajo
        else -> Color.White.copy(alpha = 0.30f)
    }

    val iconTipo = if (event.tipoEvento.uppercase() == "AVERIA") {
        R.drawable.ic_averia
    } else {
        R.drawable.ic_instalacion
    }

    val principalTitle = if (event.tipoEvento.uppercase() == "AVERIA") {
        clientName ?: "Averia tecnica"
    } else {
        "Nueva instalacion"
    }

    val locationText = if (event.tipoEvento.uppercase() == "AVERIA") {
        clientAddress ?: "Direccion no disponible"
    } else {
        extractDireccion(event.descripcion)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(AzulPrincipalOscuro, AzulPrincipal)
                    )
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ORDEN #${event.idEvento.takeLast(4)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.78f)
                )

                StatusBadge(event.estadoEvento, estadoColor)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(iconTipo),
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = event.tipoEvento.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.82f)
                    )

                    Text(
                        text = principalTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                }
            }


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_ubicacion),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.72f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = locationText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.78f)
                )
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusBadge(event.prioridad.uppercase(), prioridadColor)
                }

                Text(
                    text = formatDate(event.fechaCreacion),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.72f)
                )
            }

            Button(
                onClick = onContinueClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Turquesa,
                    contentColor = Color.White
                )
            ) {
                Text("Ver detalles")
            }
        }
    }
}

@Composable
fun TechnicianActiveJobsEmptyCard() {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "No tienes un evento activo en este momento",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )

            Text(
                text = "Cuando tomes un evento disponible, aparecera aqui como tu trabajo actual.",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

