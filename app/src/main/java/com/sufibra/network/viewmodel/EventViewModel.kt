package com.sufibra.network.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sufibra.network.data.repository.ClientRepository
import com.sufibra.network.data.repository.EventRepository
import com.sufibra.network.data.repository.UserRepository
import com.sufibra.network.domain.model.Client
import com.sufibra.network.domain.model.Event
import com.sufibra.network.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {

    private val repository = EventRepository()
    private val clientRepository = ClientRepository()
    private val userRepository = UserRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _availableEvents = MutableStateFlow<List<Event>>(emptyList())
    val availableEvents: StateFlow<List<Event>> = _availableEvents

    private val _currentTechnicianEvent = MutableStateFlow<Event?>(null)
    val currentTechnicianEvent: StateFlow<Event?> = _currentTechnicianEvent

    private val _technicianHistoryEvents = MutableStateFlow<List<Event>>(emptyList())
    val technicianHistoryEvents: StateFlow<List<Event>> = _technicianHistoryEvents

    private val _selectedEvent = MutableStateFlow<Event?>(null)
    val selectedEvent: StateFlow<Event?> = _selectedEvent

    private val _selectedClient = MutableStateFlow<Client?>(null)
    val selectedClient: StateFlow<Client?> = _selectedClient

    private val _assignedTechnician = MutableStateFlow<User?>(null)
    val assignedTechnician: StateFlow<User?> = _assignedTechnician

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> = _clients

    private val _takeEventSuccess = MutableStateFlow<Boolean?>(null)
    val takeEventSuccess: StateFlow<Boolean?> = _takeEventSuccess

    private val _startEventSuccess = MutableStateFlow<Boolean?>(null)
    val startEventSuccess: StateFlow<Boolean?> = _startEventSuccess

    private val _finalizeEventSuccess = MutableStateFlow<Boolean?>(null)
    val finalizeEventSuccess: StateFlow<Boolean?> = _finalizeEventSuccess

    private val _updateEventSuccess = MutableStateFlow<Boolean?>(null)
    val updateEventSuccess: StateFlow<Boolean?> = _updateEventSuccess

    private val _cancelEventSuccess = MutableStateFlow<Boolean?>(null)
    val cancelEventSuccess: StateFlow<Boolean?> = _cancelEventSuccess

    private val _releaseEventSuccess = MutableStateFlow<Boolean?>(null)
    val releaseEventSuccess: StateFlow<Boolean?> = _releaseEventSuccess

    suspend fun createEvent(event: Event): Result<Unit> {
        return repository.createEvent(event)
    }

    fun loadEvents() {
        viewModelScope.launch {
            val result = repository.getAllEvents()
            result.onSuccess {
                _events.value = it
            }
        }
    }

    fun loadClients() {
        viewModelScope.launch {
            val result = clientRepository.getAllClients()
            result.onSuccess {
                _clients.value = it
            }
        }
    }

    fun loadAvailableEvents() {
        viewModelScope.launch {
            val result = repository.getAvailableEvents()
            result.onSuccess {
                _availableEvents.value = it
            }
        }
    }

    fun takeEvent(event: Event, technicianId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val activeCheck = repository.getActiveEventForTechnician(technicianId)

            if (activeCheck.getOrNull() != null) {
                _errorMessage.value = "No se pudo tomar el evento porque ya tienes uno activo."
                _takeEventSuccess.value = false
                _isLoading.value = false
                return@launch
            }

            val result = repository.takeEvent(event.idEvento, technicianId)

            result.onSuccess {
                _takeEventSuccess.value = true
                loadCurrentTechnicianEvent(technicianId)
                loadAvailableEvents()
                loadEvents()
            }

            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo tomar el evento."
                _takeEventSuccess.value = false
            }

            _isLoading.value = false
        }
    }

    fun loadEventById(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.getEventById(eventId)

            result.onSuccess { event ->
                _selectedEvent.value = event

                if (event.tecnicoId != null) {
                    val techResult = userRepository.getUserByUid(event.tecnicoId)
                    techResult.onSuccess { tech ->
                        _assignedTechnician.value = tech
                    }
                    techResult.onFailure {
                        _assignedTechnician.value = null
                    }
                } else {
                    _assignedTechnician.value = null
                }
            }

            result.onFailure {
                _errorMessage.value = it.message
            }

            _isLoading.value = false
        }
    }

    fun loadClientForEvent(clientId: String) {
        viewModelScope.launch {
            val result = clientRepository.getClientById(clientId)

            result.onSuccess {
                _selectedClient.value = it
            }

            result.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearTakeEventState() {
        _takeEventSuccess.value = null
    }

    fun loadCurrentTechnicianEvent(technicianId: String) {
        viewModelScope.launch {
            val result = repository.getActiveEventForTechnician(technicianId)

            result.onSuccess {
                _currentTechnicianEvent.value = it
            }

            result.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun loadTechnicianHistoryEvents(technicianId: String) {
        viewModelScope.launch {
            val result = repository.getFinishedEventsForTechnician(technicianId)

            result.onSuccess {
                _technicianHistoryEvents.value = it
            }

            result.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun startEvent(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val technicianId = FirebaseAuth.getInstance().currentUser?.uid

            if (technicianId.isNullOrBlank()) {
                _errorMessage.value = "No se pudo identificar al técnico actual."
                _startEventSuccess.value = false
                _isLoading.value = false
                return@launch
            }

            val result = repository.startEvent(
                eventId = eventId,
                technicianId = technicianId
            )

            result.onSuccess {
                _startEventSuccess.value = true
                loadCurrentTechnicianEvent(technicianId)
                loadEventById(eventId)
                loadAvailableEvents()
                loadEvents()
            }

            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo iniciar el evento."
                _startEventSuccess.value = false
            }

            _isLoading.value = false
        }
    }

    fun clearStartEventState() {
        _startEventSuccess.value = null
    }

    fun finalizeEvent(
        eventId: String,
        solucionAplicada: String,
        observaciones: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val technicianId = FirebaseAuth.getInstance().currentUser?.uid

            if (technicianId.isNullOrBlank()) {
                _errorMessage.value = "No se pudo identificar al técnico actual."
                _finalizeEventSuccess.value = false
                _isLoading.value = false
                return@launch
            }

            if (solucionAplicada.isBlank()) {
                _errorMessage.value = "La solución aplicada es obligatoria"
                _finalizeEventSuccess.value = false
                _isLoading.value = false
                return@launch
            }

            val result = repository.finalizeEvent(
                eventId = eventId,
                technicianId = technicianId,
                solucionAplicada = solucionAplicada,
                observaciones = observaciones
            )

            result.onSuccess {
                _finalizeEventSuccess.value = true
                loadCurrentTechnicianEvent(technicianId)
                loadTechnicianHistoryEvents(technicianId)
                loadEventById(eventId)
                loadAvailableEvents()
                loadEvents()
            }

            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo finalizar el evento."
                _finalizeEventSuccess.value = false
            }

            _isLoading.value = false
        }
    }

    fun finalizeInstallationWithClient(
        eventId: String,
        solucionAplicada: String,
        observaciones: String?,
        client: Client
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val technicianId = FirebaseAuth.getInstance().currentUser?.uid

            if (technicianId.isNullOrBlank()) {
                _errorMessage.value = "No se pudo identificar al técnico actual."
                _finalizeEventSuccess.value = false
                _isLoading.value = false
                return@launch
            }

            if (solucionAplicada.isBlank()) {
                _errorMessage.value = "La solución aplicada es obligatoria"
                _finalizeEventSuccess.value = false
                _isLoading.value = false
                return@launch
            }

            val clientValidationError = validateClientForInstallation(client)
            if (clientValidationError != null) {
                _errorMessage.value = clientValidationError
                _finalizeEventSuccess.value = false
                _isLoading.value = false
                return@launch
            }

            val result = repository.finalizeInstallationWithClient(
                eventId = eventId,
                technicianId = technicianId,
                solucionAplicada = solucionAplicada,
                observaciones = observaciones,
                client = client
            )

            result.onSuccess {
                _finalizeEventSuccess.value = true
                loadCurrentTechnicianEvent(technicianId)
                loadTechnicianHistoryEvents(technicianId)
                loadEventById(eventId)
                loadAvailableEvents()
                loadEvents()
            }

            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo finalizar la instalación."
                _finalizeEventSuccess.value = false
            }

            _isLoading.value = false
        }
    }

    fun clearFinalizeEventState() {
        _finalizeEventSuccess.value = null
    }

    fun updateEvent(
        eventId: String,
        descripcion: String,
        prioridad: String
    ) {
        viewModelScope.launch {
            if (descripcion.isBlank()) {
                _errorMessage.value = "La descripción es obligatoria"
                _updateEventSuccess.value = false
                return@launch
            }

            if (prioridad !in listOf("ALTA", "MEDIA", "BAJA")) {
                _errorMessage.value = "Selecciona una prioridad válida"
                _updateEventSuccess.value = false
                return@launch
            }

            _isLoading.value = true

            val result = repository.updateEventEditableFields(
                eventId = eventId,
                descripcion = descripcion,
                prioridad = prioridad
            )

            result.onSuccess {
                _updateEventSuccess.value = true
                loadEventById(eventId)
                loadEvents()
                loadAvailableEvents()
            }

            result.onFailure {
                _errorMessage.value = it.message
                _updateEventSuccess.value = false
            }

            _isLoading.value = false
        }
    }

    fun cancelEvent(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val adminId = FirebaseAuth.getInstance().currentUser?.uid
            if (adminId.isNullOrBlank()) {
                _errorMessage.value = "No se pudo identificar al administrador que cancela el evento"
                _cancelEventSuccess.value = false
                _isLoading.value = false
                return@launch
            }

            val result = repository.cancelEvent(
                eventId = eventId,
                canceladoPor = adminId
            )

            result.onSuccess {
                _cancelEventSuccess.value = true
                loadEventById(eventId)
                loadEvents()
                loadAvailableEvents()
            }

            result.onFailure {
                _errorMessage.value = it.message
                _cancelEventSuccess.value = false
            }

            _isLoading.value = false
        }
    }

    fun releaseEvent(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val adminId = FirebaseAuth.getInstance().currentUser?.uid
            if (adminId.isNullOrBlank()) {
                _errorMessage.value = "No se pudo identificar al administrador que libera el evento"
                _releaseEventSuccess.value = false
                _isLoading.value = false
                return@launch
            }

            val result = repository.releaseTakenEvent(
                eventId = eventId,
                liberadoPor = adminId
            )

            result.onSuccess {
                _releaseEventSuccess.value = true
                loadEventById(eventId)
                loadEvents()
                loadAvailableEvents()
            }

            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo liberar el evento."
                _releaseEventSuccess.value = false
            }

            _isLoading.value = false
        }
    }

    fun clearUpdateEventState() {
        _updateEventSuccess.value = null
    }

    fun clearCancelEventState() {
        _cancelEventSuccess.value = null
    }

    fun clearReleaseEventState() {
        _releaseEventSuccess.value = null
    }

    private fun validateClientForInstallation(client: Client): String? {
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
