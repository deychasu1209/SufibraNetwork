package com.sufibra.network.ui.components.clients

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sufibra.network.BuildConfig
import com.sufibra.network.R
import com.sufibra.network.ui.components.clients.ClientFacadePhotoSection

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
    onPhotoSelected: (Uri) -> Unit,
    onPhotoRemoved: () -> Unit,
    isPhotoUploading: Boolean,
    photoUploadError: String?,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    var mapsPickerError by remember { mutableStateOf<String?>(null) }
    var showMapPicker by remember { mutableStateOf(false) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onPhotoSelected(uri)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = nombresApellidos,
            onValueChange = onNombresApellidosChange,
            label = { Text("Nombre y apellidos") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
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
            label = { Text("Dirección") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = referencia,
            onValueChange = onReferenciaChange,
            label = { Text("Referencia") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = zona,
            onValueChange = onZonaChange,
            label = { Text("Zona") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
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
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (BuildConfig.MAPS_API_KEY.isBlank()) {
                            mapsPickerError =
                                "Configura MAPS_API_KEY en local.properties para usar el selector."
                            Toast
                                .makeText(
                                    context,
                                    "Falta configurar MAPS_API_KEY",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                            return@IconButton
                        }

                        mapsPickerError = null
                        showMapPicker = true
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_ubicacion),
                        contentDescription = "Seleccionar ubicación",
                        tint = colorScheme.primary
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        mapsPickerError?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.error
            )
        }

        ClientFacadePhotoSection(
            photoUrl = fotoFachada,
            accentColor = colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    imagePickerLauncher.launch("image/*")
                },
                modifier = Modifier.weight(1f),
                enabled = !isPhotoUploading
            ) {
                if (isPhotoUploading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (fotoFachada.isBlank()) "Subir foto" else "Cambiar foto")
                }
            }

            if (fotoFachada.isNotBlank()) {
                OutlinedButton(
                    onClick = onPhotoRemoved,
                    modifier = Modifier.weight(1f),
                    enabled = !isPhotoUploading
                ) {
                    Text("Quitar foto")
                }
            }
        }

        if (isPhotoUploading) {
            Text(
                text = "Subiendo foto de fachada...",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
        }

        photoUploadError?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.error
            )
        }

        Text(
            text = "El DNI debe tener 8 dígitos y el celular 9. Los campos de referencia, caja NAP, puerto NAP y Link Maps pueden quedar vacíos. La foto se sube automáticamente y se guarda como enlace.",
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant
        )
    }

    if (showMapPicker) {
        MapLocationPickerDialog(
            currentLinkMaps = linkMaps,
            onDismiss = { showMapPicker = false },
            onLocationSelected = { mapsLink ->
                onLinkMapsChange(mapsLink)
                mapsPickerError = null
                showMapPicker = false
            }
        )
    }
}

