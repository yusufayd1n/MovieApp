package com.example.movieapp.ui.feature.auth.register

import androidx.lifecycle.viewModelScope
import com.example.movieapp.R
import com.example.movieapp.common.ui.BaseViewModel
import com.example.movieapp.common.ui.UiEvent
import com.example.movieapp.ui.navigation.screen.Screen
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onEmailChange(newValue: String) {
        _state.update { it.copy(email = newValue) }
    }

    fun onPasswordChange(newValue: String) {
        _state.update { it.copy(password = newValue) }
    }

    fun onConfirmPasswordChange(newValue: String) {
        _state.update { it.copy(confirmPassword = newValue) }
    }

    fun register() {
        val currentState = _state.value

        if (currentState.email.isBlank() || currentState.password.isBlank() || currentState.confirmPassword.isBlank()) {
            showSnackbar(R.string.fill_all_fields_error)
            return
        }

        if (currentState.password != currentState.confirmPassword) {
            showSnackbar(R.string.passwords_do_not_match_error)
            return
        }

        if (currentState.password.length < 6) {
            showSnackbar(R.string.password_length_error)
            return
        }

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(currentState.email, currentState.password)
                .addOnSuccessListener {
                    _state.update { it.copy(isLoading = false) }
                    showSnackbar(R.string.registration_success)
                    sendEvent(UiEvent.Navigate(Screen.Login))
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