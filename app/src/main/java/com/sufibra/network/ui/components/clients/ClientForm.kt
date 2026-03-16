package com.sufibra.network.ui.components.clients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ClientForm(
    nombresApellidos: String,
    dni: String,
    celular: String,
    direccion: String,
    referencia: String,
    zona: String,
    cajaNap: String,
    puertoNap: String,
    linkMaps: String,
    fotoFachada: String,
    onNombresApellidosChange: (String) -> Unit,
    onDniChange: (String) -> Unit,
    onCelularChange: (String) -> Unit,
    onDireccionChange: (String) -> Unit,
    onReferenciaChange: (String) -> Unit,
    onZonaChange: (String) -> Unit,
    onCajaNapChange: (String) -> Unit,
    onPuertoNapChange: (String) -> Unit,
    onLinkMapsChange: (String) -> Unit,
    onFotoFachadaChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = nombresApellidos,
            onValueChange = onNombresApellidosChange,
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dni,
            onValueChange = { onDniChange(it.filter(Char::isDigit).take(8)) },
            label = { Text("DNI") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = celular,
            onValueChange = { onCelularChange(it.filter(Char::isDigit).take(9)) },
            label = { Text("Celular") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = direccion,
            onValueChange = onDireccionChange,
            label = { Text("Direccion") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = referencia,
            onValueChange = onReferenciaChange,
            label = { Text("Referencia") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = zona,
            onValueChange = onZonaChange,
            label = { Text("Zona") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cajaNap,
            onValueChange = onCajaNapChange,
            label = { Text("Caja NAP") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = puertoNap,
            onValueChange = onPuertoNapChange,
            label = { Text("Puerto NAP") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = linkMaps,
            onValueChange = onLinkMapsChange,
            label = { Text("Link Maps") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fotoFachada,
            onValueChange = onFotoFachadaChange,
            label = { Text("Foto fachada") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Los campos de referencia, caja NAP, puerto NAP, Link Maps y foto pueden quedar vacios.",
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant
        )
    }
}
