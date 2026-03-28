package com.sufibra.network.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sufibra.network.data.repository.AuthRepository
import com.sufibra.network.data.repository.UserRepository
import com.sufibra.network.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRecoveryLoading = MutableStateFlow(false)
    val isRecoveryLoading: StateFlow<Boolean> = _isRecoveryLoading

    private val _loggedUser = MutableStateFlow<User?>(null)
    val loggedUser: StateFlow<User?> = _loggedUser

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _recoveryErrorMessage = MutableStateFlow<String?>(null)
    val recoveryErrorMessage: StateFlow<String?> = _recoveryErrorMessage

    private val _recoverySuccessMessage = MutableStateFlow<String?>(null)
    val recoverySuccessMessage: StateFlow<String?> = _recoverySuccessMessage

    fun login(email: String, password: String) {
        if (email.isBlank()) {
            _errorMessage.value = "El correo es obligatorio"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Correo inválido"
            return
        }

        if (password.isBlank()) {
            _errorMessage.value = "La contraseña es obligatoria"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val authResult = authRepository.login(email, password)

            authResult.onSuccess { uid ->
                val userResult = userRepository.getUserByUid(uid)

                userResult.onSuccess { user ->
                    if (user.estado) {
                        _loggedUser.value = user
                    } else {
                        authRepository.logout()
                        _errorMessage.value = "Usuario inactivo"
                    }
                }

                userResult.onFailure { exception ->
                    _errorMessage.value = exception.message
                }
            }

            authResult.onFailure {
                _errorMessage.value = "Credenciales incorrectas"
            }

            _isLoading.value = false
        }
    }

    fun sendPasswordRecovery(email: String) {
        if (email.isBlank()) {
            _recoveryErrorMessage.value = "El correo es obligatorio"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _recoveryErrorMessage.value = "Correo inválido"
            return
        }

        viewModelScope.launch {
            _isRecoveryLoading.value = true
            _recoveryErrorMessage.value = null
            _recoverySuccessMessage.value = null

            val result = authRepository.sendPasswordResetEmail(email.trim())

            result.onSuccess {
                _recoverySuccessMessage.value =
                    "Si existe una cuenta asociada a ese correo, recibirás instrucciones para restablecer tu contraseña."
            }

            result.onFailure {
                _recoveryErrorMessage.value =
                    "No se pudo enviar el correo de recuperación. Inténtalo nuevamente."
            }

            _isRecoveryLoading.value = false
        }
    }

    fun resetLoginState() {
        _loggedUser.value = null
    }

    fun resetError() {
        _errorMessage.value = null
    }

    fun clearRecoveryError() {
        _recoveryErrorMessage.value = null
    }

    fun clearRecoverySuccess() {
        _recoverySuccessMessage.value = null
    }
}
