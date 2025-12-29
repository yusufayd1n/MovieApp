package com.example.movieapp.ui.feature.auth.login

import androidx.lifecycle.viewModelScope
import com.example.movieapp.R
import com.example.movieapp.common.ui.BaseViewModel
import com.example.movieapp.common.ui.UiEvent
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEmailChange(newValue: String) {
        _state.update { it.copy(email = newValue) }
    }

    fun onPasswordChange(newValue: String) {
        _state.update { it.copy(password = newValue) }
    }

    fun login() {
        val currentState = _state.value
        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            showSnackbar(R.string.fill_all_fields_error)
            return
        }

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            auth.signInWithEmailAndPassword(currentState.email, currentState.password)
                .addOnSuccessListener {
                    _state.update { it.copy(isLoading = false) }
                    sendEvent(UiEvent.PopBackStack)
                }
                .addOnFailureListener { exception ->
                    _state.update { it.copy(isLoading = false) }
                    showSnackbar(
                        messageResId = R.string.error_unknown,
                        remoteMessage = exception.localizedMessage
                    )
                }
        }
    }
}