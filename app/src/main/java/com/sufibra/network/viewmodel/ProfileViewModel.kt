package com.sufibra.network.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sufibra.network.data.repository.AuthRepository
import com.sufibra.network.data.repository.UserRepository
import com.sufibra.network.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val authRepository = AuthRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _profileUpdated = MutableStateFlow<Boolean?>(null)
    val profileUpdated: StateFlow<Boolean?> = _profileUpdated

    private val _passwordChanged = MutableStateFlow<Boolean?>(null)
    val passwordChanged: StateFlow<Boolean?> = _passwordChanged

    fun loadCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true

            val result = userRepository.getCurrentUser()

            result.onSuccess {
                _currentUser.value = it
            }

            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo cargar el perfil"
            }

            _isLoading.value = false
        }
    }

    fun updateOwnProfile(
        nombres: String,
        apellidos: String,
        telefono: String
    ) {
        val normalizedNames = normalizeSpaces(nombres)
        val normalizedLastNames = normalizeSpaces(apellidos)
        val normalizedPhone = telefono.trim()

        if (normalizedNames.isBlank()) {
            _errorMessage.value = "Los nombres son obligatorios"
            _profileUpdated.value = false
            return
        }

        if (normalizedLastNames.isBlank()) {
            _errorMessage.value = "Los apellidos son obligatorios"
            _profileUpdated.value = false
            return
        }

        if (normalizedPhone.isNotBlank()) {
            val sanitized = normalizedPhone.replace(" ", "").removePrefix("+51")
            if (sanitized.any { !it.isDigit() }) {
                _errorMessage.value = "El teléfono solo debe contener números"
                _profileUpdated.value = false
                return
            }
            if (sanitized.length != 9) {
                _errorMessage.value = "El teléfono debe tener 9 dígitos"
                _profileUpdated.value = false
                return
            }
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val telefonoToSave = if (normalizedPhone.isBlank()) {
                null
            } else {
                val digitsOnly = normalizedPhone.replace(" ", "").removePrefix("+51")
                "+51 $digitsOnly"
            }

            val result = userRepository.updateOwnProfile(
                nombres = normalizedNames,
                apellidos = normalizedLastNames,
                telefono = telefonoToSave
            )

            result.onSuccess {
                _profileUpdated.value = true
                loadCurrentUser()
            }

            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo actualizar el perfil"
                _profileUpdated.value = false
            }

            _isLoading.value = false
        }
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        if (currentPassword.isBlank()) {
            _errorMessage.value = "La contraseña actual es obligatoria"
            _passwordChanged.value = false
            return
        }

        if (newPassword.length < 6) {
            _errorMessage.value = "La nueva contraseña debe tener al menos 6 caracteres"
            _passwordChanged.value = false
            return
        }

        if (newPassword != confirmPassword) {
            _errorMessage.value = "La confirmación no coincide con la nueva contraseña"
            _passwordChanged.value = false
            return
        }

        if (currentPassword == newPassword) {
            _errorMessage.value = "La nueva contraseña debe ser diferente a la actual"
            _passwordChanged.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authRepository.changeCurrentUserPassword(
                currentPassword = currentPassword,
                newPassword = newPassword
            )

            result.onSuccess {
                _passwordChanged.value = true
            }

            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo cambiar la contraseña"
                _passwordChanged.value = false
            }

            _isLoading.value = false
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetProfileUpdatedState() {
        _profileUpdated.value = null
    }

    fun resetPasswordChangedState() {
        _passwordChanged.value = null
    }

    private fun normalizeSpaces(value: String): String {
        return value.trim().replace("\\s+".toRegex(), " ")
    }
}
