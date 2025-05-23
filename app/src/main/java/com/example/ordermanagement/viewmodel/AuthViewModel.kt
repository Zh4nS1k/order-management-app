package com.example.ordermanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ordermanagement.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<Result<Unit>?>(null)
    val authState: StateFlow<Result<Unit>?> = _authState.asStateFlow()

    private val _userRole = MutableStateFlow("")
    val userRole = _userRole.asStateFlow()

    fun register(email: String, password: String, name: String, role: String = "user") {
        viewModelScope.launch {
            _authState.value = repository.register(email, password, name, role)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            if (result.isSuccess) {
                val role = repository.getRole() ?: ""
                _userRole.value = role
                _authState.value = Result.success(Unit)
            } else {
                _authState.value = result
            }
        }
    }

    fun resetAuthState() {
        _authState.value = null
    }

    fun logout() = repository.logout()
}
