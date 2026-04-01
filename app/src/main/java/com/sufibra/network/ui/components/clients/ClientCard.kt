package com.sufibra.network.ui.components.clients

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sufibra.network.R
import com.sufibra.network.domain.model.Client
import com.sufibra.network.ui.components.clients.ClientFacadePhotoSection

@Composable
fun ClientCard(
    client: Client,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    onEdit: () -> Unit,
    onToggleStatus: (Boolean) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val statusColor = if (client.estadoCliente) colorScheme.primary else colorScheme.outline
    val statusText = if (client.estadoCliente) "Activo" else "Inactivo"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpandedChange() },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_persona),
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = client.nombresApellidos,
                            style = MaterialTheme.typography.titleMedium,
                            color = colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_ubicacion),
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = client.direccion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_telefono),
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = client.celular,
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_zona),
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = client.zona,
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ClientStatusBadge(
                        text = statusText,
                        active = client.estadoCliente,
                        color = statusColor
                    )

                    Text(
                        text = if (expanded) "▲" else "▼",
                        style = MaterialTheme.typography.labelLarge,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(color = colorScheme.outlineVariant)

                    Text("DNI: ${client.dni}", color = colorScheme.onSurface)
                    Text("Celular: ${client.celular}", color = colorScheme.onSurface)
                    Text("Direccion: ${client.direccion}", color = colorScheme.onSurface)
                    Text("Referencia: ${client.referencia}", color = colorScheme.onSurface)
                    Text("Zona: ${client.zona}", color = colorScheme.onSurface)
                    Text("Caja NAP: ${client.cajaNAP}", color = colorScheme.onSurface)
                    Text("Puerto NAP: ${client.puertoNAP}", color = colorScheme.onSurface)

                    ClientFacadePhotoSection(
                        photoUrl = client.fotoFachada,
                        accentColor = statusColor
                    )

                    Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        color = colorScheme.primaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (client.linkMaps.isBlank()) return@clickable
                                val uri = Uri.parse(client.linkMaps)
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(intent)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_ubicacion),
                                contentDescription = "Ubicacion",
                                tint = colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = if (client.linkMaps.isBlank()) "Link Maps no disponible" else "Ver ubicacion en el mapa",
                                color = colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onEdit,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Editar")
                        }

                        OutlinedButton(
                            onClick = { onToggleStatus(!client.estadoCliente) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (client.estadoCliente) "Desactivar" else "Activar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientStatusBadge(
    text: String,
    active: Boolean,
    color: androidx.compose.ui.graphics.Color
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
        color = if (active) color.copy(alpha = 0.14f) else colorScheme.surface
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}

