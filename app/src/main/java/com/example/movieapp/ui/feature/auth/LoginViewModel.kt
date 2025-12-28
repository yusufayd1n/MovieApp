package com.example.movieapp.ui.feature.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.movieapp.R
import com.example.movieapp.common.BaseViewModel
import com.example.movieapp.common.UiEvent
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel() {

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun onEmailChange(newValue: String) {
        email = newValue
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
    }

    fun login() {
        if (email.isBlank() || password.isBlank()) {
            showSnackbar(R.string.fill_all_fields_error)
            return
        }

        isLoading = true

        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    isLoading = false
                    sendEvent(UiEvent.PopBackStack)
                }
                .addOnFailureListener { exception ->
                    isLoading = false
                    val errorMessage = exception.localizedMessage
                    showSnackbar(
                        messageResId = R.string.error_unknown,
                        remoteMessage = errorMessage
                    )
                }
        }
    }
}