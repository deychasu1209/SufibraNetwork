package com.sufibra.network.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sufibra.network.data.repository.ClientRepository
import com.sufibra.network.data.repository.StorageRepository
import com.sufibra.network.domain.model.Client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientsViewModel : ViewModel() {

    private val repository = ClientRepository()
    private val storageRepository = StorageRepository()

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> = _clients

    private val _selectedClient = MutableStateFlow<Client?>(null)
    val selectedClient: StateFlow<Client?> = _selectedClient

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _operationSuccess = MutableStateFlow<Boolean?>(null)
    val operationSuccess: StateFlow<Boolean?> = _operationSuccess

    private val _isPhotoUploading = MutableStateFlow(false)
    val isPhotoUploading: StateFlow<Boolean> = _isPhotoUploading

    private val _photoUploadError = MutableStateFlow<String?>(null)
    val photoUploadError: StateFlow<String?> = _photoUploadError

    private val _uploadedPhotoUrl = MutableStateFlow<String?>(null)
    val uploadedPhotoUrl: StateFlow<String?> = _uploadedPhotoUrl

    fun loadClients() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getAllClients()
            result.onSuccess {
                _clients.value = it
            }
            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudieron cargar los clientes."
            }
            _isLoading.value = false
        }
    }

    fun loadClientById(clientId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getClientById(clientId)
            result.onSuccess {
                _selectedClient.value = it
            }
            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo cargar el cliente."
            }
            _isLoading.value = false
        }
    }

    fun createClient(client: Client) {
        val normalizedClient = normalizeClient(client)
        val validationError = validateClient(normalizedClient)
        if (validationError != null) {
            _errorMessage.value = validationError
            _operationSuccess.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.createClient(normalizedClient)
            result.onSuccess {
                _operationSuccess.value = true
                loadClients()
            }
            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo crear el cliente."
                _operationSuccess.value = false
            }
            _isLoading.value = false
        }
    }

    fun updateClient(client: Client) {
        val normalizedClient = normalizeClient(client)
        val validationError = validateClient(normalizedClient)
        if (validationError != null) {
            _errorMessage.value = validationError
            _operationSuccess.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateClient(normalizedClient)
            result.onSuccess {
                _selectedClient.value = normalizedClient
                _operationSuccess.value = true
                loadClients()
            }
            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo actualizar el cliente."
                _operationSuccess.value = false
            }
            _isLoading.value = false
        }
    }

    fun updateClientStatus(clientId: String, estadoCliente: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateClientStatus(clientId, estadoCliente)
            result.onSuccess {
                _operationSuccess.value = true
                loadClients()
            }
            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo actualizar el estado del cliente."
                _operationSuccess.value = false
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun uploadClientFacadePhoto(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            _isPhotoUploading.value = true
            _photoUploadError.value = null

            val result = storageRepository.uploadClientFacadePhoto(context, imageUri)
            result.onSuccess {
                _uploadedPhotoUrl.value = it
            }
            result.onFailure {
                _photoUploadError.value =
                    it.message ?: "No se pudo subir la foto de fachada."
            }

            _isPhotoUploading.value = false
        }
    }

    fun consumeUploadedPhotoUrl() {
        _uploadedPhotoUrl.value = null
    }

    fun clearPhotoUploadState() {
        _photoUploadError.value = null
        _uploadedPhotoUrl.value = null
    }

    fun resetOperationState() {
        _operationSuccess.value = null
    }

    private fun normalizeClient(client: Client): Client {
        return client.copy(
            nombresApellidos = normalizeSpaces(client.nombresApellidos),
            dni = client.dni.trim(),
            celular = client.celular.trim(),
            direccion = normalizeSpaces(client.direccion),
            referencia = normalizeSpaces(client.referencia),
            zona = normalizeSpaces(client.zona),
            cajaNAP = normalizeSpaces(client.cajaNAP),
            puertoNAP = normalizeSpaces(client.puertoNAP),
            linkMaps = client.linkMaps.trim(),
            fotoFachada = client.fotoFachada.trim()
        )
    }

    private fun validateClient(client: Client): String? {
        val fullNameParts = client.nombresApellidos.split("\\s+".toRegex()).filter { it.isNotBlank() }

        return when {
            client.nombresApellidos.isBlank() -> "El nombre completo es obligatorio."
            client.nombresApellidos.length < 6 || fullNameParts.size < 2 ->
                "Ingresa nombre y apellidos válidos."
            client.dni.isBlank() -> "El DNI es obligatorio."
            client.dni.any { !it.isDigit() } || client.dni.length != 8 ->
                "El DNI debe tener exactamente 8 dígitos."
            client.celular.isBlank() -> "El celular es obligatorio."
            client.celular.any { !it.isDigit() } || client.celular.length != 9 ->
                "El celular debe tener exactamente 9 dígitos."
            !client.celular.startsWith("9") ->
                "El celular debe comenzar con 9."
            client.direccion.isBlank() -> "La dirección es obligatoria."
            client.direccion.length < 8 ->
                "La dirección debe tener al menos 8 caracteres."
            client.zona.isBlank() -> "La zona es obligatoria."
            client.zona.length < 2 ->
                "La zona debe tener al menos 2 caracteres."
            client.linkMaps.isNotBlank() && !isValidMapsUrl(client.linkMaps) ->
                "Ingresa un enlace válido para Google Maps."
            client.fotoFachada.isNotBlank() && !isValidHttpUrl(client.fotoFachada) ->
                "Ingresa una URL válida para la foto de fachada."
            else -> null
        }
    }

    private fun normalizeSpaces(value: String): String {
        return value.trim().replace("\\s+".toRegex(), " ")
    }

    private fun isValidHttpUrl(value: String): Boolean {
        return Patterns.WEB_URL.matcher(value).matches() &&
            (value.startsWith("http://") || value.startsWith("https://"))
    }

    private fun isValidMapsUrl(value: String): Boolean {
        if (!isValidHttpUrl(value)) return false

        val normalized = value.lowercase()
        return normalized.contains("google.com/maps") ||
            normalized.contains("maps.app.goo.gl") ||
            normalized.contains("google.com/maps/search")
    }
}
