package com.sufibra.network.ui.components.clients

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sufibra.network.R
import com.sufibra.network.domain.model.Client
import java.text.Normalizer

@Composable
fun SearchableClientPickerDialog(
    clients: List<Client>,
    selectedClientId: String?,
    onDismiss: () -> Unit,
    onClientSelected: (Client) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var searchQuery by remember { mutableStateOf("") }

    val filteredClients = remember(clients, searchQuery) {
        if (searchQuery.isBlank()) {
            clients
        } else {
            val query = normalizeClientSearchText(searchQuery)
            clients.filter { client ->
                normalizeClientSearchText(client.nombresApellidos).contains(query) ||
                    normalizeClientSearchText(client.dni).contains(query) ||
                    normalizeClientSearchText(client.celular).contains(query) ||
                    normalizeClientSearchText(client.direccion).contains(query) ||
                    normalizeClientSearchText(client.zona).contains(query)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Seleccionar cliente",
                    style = MaterialTheme.typography.titleLarge,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Busca por nombre, DNI, celular, dirección o zona.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Buscar cliente") },
                    placeholder = { Text("Escribe para filtrar...") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_lupa),
                            contentDescription = null
                        )
                    },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (filteredClients.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(
                                color = colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(18.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_clientes),
                                contentDescription = null,
                                tint = colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "No se encontraron clientes",
                                style = MaterialTheme.typography.titleMedium,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = "Prueba con otro nombre, DNI o dirección.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.65f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredClients, key = { it.idCliente }) { client ->
                            SearchableClientRow(
                                client = client,
                                selected = client.idCliente == selectedClientId,
                                onClick = { onClientSelected(client) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchableClientRow(
    client: Client,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                colorScheme.primaryContainer
            } else {
                colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (selected) {
                    colorScheme.primary.copy(alpha = 0.16f)
                } else {
                    colorScheme.surface
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_persona),
                    contentDescription = null,
                    tint = if (selected) colorScheme.primary else colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(18.dp)
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = client.nombresApellidos,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (selected) colorScheme.onPrimaryContainer else colorScheme.onSurface
                )

                Text(
                    text = "DNI: ${client.dni} · Cel: ${client.celular}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant
                )

                Text(
                    text = client.direccion,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant
                )

                Text(
                    text = client.zona,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected) colorScheme.primary else colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun normalizeClientSearchText(value: String): String {
    return Normalizer
        .normalize(value.trim().lowercase(), Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
}
