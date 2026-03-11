package com.sufibra.network.viewmodel

import androidx.lifecycle.ViewModel
import com.sufibra.network.data.repository.AuthRepository

class SessionViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    fun logout() {
        authRepository.logout()
    }
}
