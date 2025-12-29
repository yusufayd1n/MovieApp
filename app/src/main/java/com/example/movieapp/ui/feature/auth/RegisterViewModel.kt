package com.example.movieapp.ui.feature.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.movieapp.R
import com.example.movieapp.common.ui.BaseViewModel
import com.example.movieapp.common.ui.UiEvent
import com.example.movieapp.ui.navigation.screen.Screen
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel() {

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var confirmPassword by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun onEmailChange(newValue: String) { email = newValue }
    fun onPasswordChange(newValue: String) { password = newValue }
    fun onConfirmPasswordChange(newValue: String) { confirmPassword = newValue }

    fun register() {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            showSnackbar(R.string.fill_all_fields_error)
            return
        }

        if (password != confirmPassword) {
            showSnackbar(R.string.passwords_do_not_match_error)
            return
        }

        if (password.length < 6) {
            showSnackbar(R.string.password_length_error)
            return
        }

        isLoading = true

        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    isLoading = false
                    showSnackbar(R.string.registration_success)

                    sendEvent(UiEvent.Navigate(Screen.Login))
                }
                .addOnFailureListener { exception ->
                    isLoading = false
                    showSnackbar(
                        messageResId = R.string.error_unknown,
                        remoteMessage = exception.localizedMessage
                    )
                }
        }
    }
}