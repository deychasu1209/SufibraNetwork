package com.sufibra.network.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sufibra.network.data.repository.UserRepository
import com.sufibra.network.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsersViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _operationSuccess = MutableStateFlow<Boolean?>(null)
    val operationSuccess: StateFlow<Boolean?> = _operationSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun createUser(
        context: Context,
        nombres: String,
        apellidos: String,
        correo: String,
        password: String,
        confirmPassword: String,
        rol: String,
        telefono: String? = null,
        zonaAsignada: String? = null
    ) {
        if (
            nombres.isBlank() ||
            apellidos.isBlank() ||
            correo.isBlank() ||
            password.isBlank() ||
            confirmPassword.isBlank()
        ) {
            _errorMessage.value = "Todos los campos obligatorios deben completarse."
            return
        }

        if (password != confirmPassword) {
            _errorMessage.value = "Las contraseñas no coinciden."
            return
        }

        if (password.length < 6) {
            _errorMessage.value = "La contraseña debe tener al menos 6 caracteres."
            return
        }

        if (!telefono.isNullOrBlank()) {
            if (telefono.length != 9) {
                _errorMessage.value = "El teléfono debe tener 9 dígitos."
                return
            }

            if (!telefono.all { it.isDigit() }) {
                _errorMessage.value = "El teléfono solo debe contener números."
                return
            }
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val telefonoFormateado = if (!telefono.isNullOrBlank()) {
                "+51 $telefono"
            } else {
                null
            }

            val result = userRepository.createUser(
                context,
                nombres,
                apellidos,
                correo,
                password,
                rol,
                telefonoFormateado,
                zonaAsignada
            )

            result.onSuccess {
                _operationSuccess.value = true
                loadUsers()
            }

            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo crear el usuario. Inténtalo nuevamente."
                _operationSuccess.value = false
            }

            _isLoading.value = false
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true

            val result = userRepository.getAllUsers()

            result.onSuccess {
                _users.value = it
            }

            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudieron cargar los usuarios."
            }

            _isLoading.value = false
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = userRepository.updateUser(user)

            result.onSuccess {
                _operationSuccess.value = true
                loadUsers()
            }

            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo actualizar el usuario."
                _operationSuccess.value = false
            }

            _isLoading.value = false
        }
    }

    fun updateUserStatus(uid: String, estado: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = userRepository.updateUserStatus(uid, estado)

            result.onSuccess {
                loadUsers()
            }

            result.onFailure {
                _errorMessage.value = it.message ?: "No se pudo actualizar el estado del usuario."
            }

            _isLoading.value = false
        }
    }

    fun resetOperationState() {
        _operationSuccess.value = null
    }
}
