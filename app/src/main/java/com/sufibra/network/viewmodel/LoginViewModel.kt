package com.sufibra.network.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sufibra.network.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.sufibra.network.data.repository.UserRepository
import com.sufibra.network.domain.model.User


class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loggedUser = MutableStateFlow<User?>(null)
    val loggedUser: StateFlow<User?> = _loggedUser

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun login(email: String, password: String) {

        if (email.isBlank()) {
            _errorMessage.value = "El correo es obligatorio"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

            authResult.onFailure { exception ->
                _errorMessage.value = "Credenciales incorrectas"
            }

            _isLoading.value = false
        }
    }


    fun resetLoginState() {
        _loggedUser.value = null
    }

    fun resetError() {
        _errorMessage.value = null
    }

}

