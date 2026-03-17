package com.sufibra.network.ui.components.users

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sufibra.network.R
import com.sufibra.network.domain.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UserCard(
    user: User,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    onEdit: () -> Unit,
    onToggleStatus: (Boolean) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val roleAccent = when (user.rol) {
        "ADMIN" -> colorScheme.secondary
        else -> colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpandedChange() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(roleAccent)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = roleAccent.copy(alpha = 0.14f)
                            ) {
                                Icon(
                                    painter = painterResource(id = if (user.rol == "ADMIN") R.drawable.ic_perfil else R.drawable.ic_tecnico),
                                    contentDescription = null,
                                    tint = roleAccent,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "${user.nombres} ${user.apellidos}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = user.correo,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UserRoleBadge(user.rol)
                        UserStatusBadge(user.estado)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    UserInfoLine(
                        iconRes = R.drawable.ic_telefono,
                        text = user.telefono ?: "Sin teléfono",
                        modifier = Modifier.weight(1f)
                    )

                    UserInfoLine(
                        iconRes = R.drawable.ic_zona,
                        text = if (user.rol == "TECHNICIAN") user.zonaAsignada ?: "Sin zona" else "Administrador",
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = if (expanded) "▲ Ocultar detalles" else "▼ Ver detalles",
                    style = MaterialTheme.typography.labelLarge,
                    color = colorScheme.onSurfaceVariant
                )

                AnimatedVisibility(visible = expanded) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HorizontalDivider(color = colorScheme.outlineVariant)

                        UserDetailRow(label = "Rol", value = roleLabel(user.rol))
                        UserDetailRow(label = "Estado", value = if (user.estado) "Activo" else "Inactivo")
                        UserDetailRow(label = "ID Usuario", value = user.idUsuario)
                        UserDetailRow(label = "Creado", value = formatDate(user.fechaCreacion))

                        if (user.rol == "TECHNICIAN") {
                            UserDetailRow(
                                label = "Disponibilidad",
                                value = if (user.disponible == true) "Disponible" else "No disponible"
                            )
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
                                onClick = { onToggleStatus(!user.estado) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (user.estado) "Desactivar" else "Activar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserInfoLine(
    iconRes: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun UserDetailRow(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurface
        )
    }
}

@Composable
private fun UserRoleBadge(role: String) {
    val colorScheme = MaterialTheme.colorScheme
    val background = if (role == "ADMIN") colorScheme.secondaryContainer else colorScheme.primaryContainer
    val content = if (role == "ADMIN") colorScheme.onSecondaryContainer else colorScheme.onPrimaryContainer

    Surface(
        shape = RoundedCornerShape(50),
        color = background
    ) {
        Text(
            text = roleLabel(role),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = content
        )
    }
}

@Composable
private fun UserStatusBadge(active: Boolean) {
    val colorScheme = MaterialTheme.colorScheme
    val background = if (active) colorScheme.tertiaryContainer else colorScheme.surface
    val content = if (active) colorScheme.onTertiaryContainer else colorScheme.onSurfaceVariant

    Surface(
        shape = RoundedCornerShape(50),
        color = background,
        tonalElevation = if (active) 0.dp else 1.dp
    ) {
        Text(
            text = if (active) "Activo" else "Inactivo",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = content
        )
    }
}

private fun roleLabel(role: String): String {
    return if (role == "ADMIN") "Administrador" else "Técnico"
}

private fun formatDate(timestamp: Long): String {
    if (timestamp <= 0L) return "Sin registro"
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-PE"))
    return formatter.format(Date(timestamp))
}
