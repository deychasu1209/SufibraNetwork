package com.sufibra.network.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sufibra.network.data.repository.ClientRepository
import com.sufibra.network.domain.model.Client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientsViewModel : ViewModel() {

    private val repository = ClientRepository()

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

    fun loadClients() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getAllClients()
            result.onSuccess {
                _clients.value = it
            }
            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudieron cargar los clientes"
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
                _errorMessage.value = it.message ?: "No se pudo cargar el cliente"
            }
            _isLoading.value = false
        }
    }

    fun createClient(client: Client) {
        val validationError = validateClient(client)
        if (validationError != null) {
            _errorMessage.value = validationError
            _operationSuccess.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.createClient(client)
            result.onSuccess {
                _operationSuccess.value = true
                loadClients()
            }
            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo crear el cliente"
                _operationSuccess.value = false
            }
            _isLoading.value = false
        }
    }

    fun updateClient(client: Client) {
        val validationError = validateClient(client)
        if (validationError != null) {
            _errorMessage.value = validationError
            _operationSuccess.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateClient(client)
            result.onSuccess {
                _selectedClient.value = client
                _operationSuccess.value = true
                loadClients()
            }
            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo actualizar el cliente"
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
                _errorMessage.value = it.message ?: "No se pudo actualizar el estado del cliente"
                _operationSuccess.value = false
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetOperationState() {
        _operationSuccess.value = null
    }

    private fun validateClient(client: Client): String? {
        return when {
            client.nombresApellidos.isBlank() -> "El nombre del cliente es obligatorio"
            client.dni.isBlank() -> "El DNI es obligatorio"
            client.dni.any { !it.isDigit() } -> "El DNI debe contener solo números"
            client.dni.length < 8 -> "El DNI debe tener al menos 8 dígitos"
            client.celular.isBlank() -> "El celular es obligatorio"
            client.celular.any { !it.isDigit() } -> "El celular debe contener solo números"
            client.celular.length < 9 -> "El celular debe tener al menos 9 dígitos"
            client.direccion.isBlank() -> "La dirección es obligatoria"
            client.zona.isBlank() -> "La zona es obligatoria"
            else -> null
        }
    }
}
