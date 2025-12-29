package com.example.movieapp.ui.feature.settings

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.movieapp.R
import com.example.movieapp.common.ui.BaseViewModel
import com.example.movieapp.common.utl.LocaleHelper
import com.example.movieapp.data.repository.SettingsRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        _state.update { it.copy(currentLanguage = LocaleHelper.getLanguage(context)) }

        observeTheme()
        observeAuthState()
    }

    private fun observeTheme() {
        viewModelScope.launch {
            settingsRepository.isDarkTheme.collect { isDark ->
                _state.update { it.copy(isDarkTheme = isDark) }
            }
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            callbackFlow {
                val listener = FirebaseAuth.AuthStateListener { auth ->
                    trySend(auth.currentUser)
                }
                auth.addAuthStateListener(listener)
                awaitClose { auth.removeAuthStateListener(listener) }
            }.collect { user ->
                _state.update { it.copy(currentUser = user) }
            }
        }
    }

    fun showPasswordDialog() {
        _state.update { it.copy(isPasswordDialogVisible = true) }
    }

    fun hidePasswordDialog() {
        _state.update { it.copy(isPasswordDialogVisible = false) }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser
        val email = user?.email

        if (user == null || email == null) return

        if (currentPassword.isBlank() || newPassword.isBlank()) {
            showSnackbar(R.string.fill_all_fields_error)
            return
        }

        if (newPassword.length < 6) {
            showSnackbar(messageResId = R.string.password_length_error)
            return
        }

        _state.update { it.copy(isLoading = true) }

        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        _state.update { it.copy(isLoading = false, isPasswordDialogVisible = false) }
                        showSnackbar(messageResId = R.string.password_reset_succes)
                    }
                    .addOnFailureListener { e ->
                        _state.update { it.copy(isLoading = false) }
                        showSnackbar(messageResId = R.string.error_unknown, remoteMessage = e.localizedMessage)
                    }
            }
            .addOnFailureListener { e ->
                _state.update { it.copy(isLoading = false) }
                showSnackbar(messageResId = R.string.error_unknown, remoteMessage = e.localizedMessage)
            }
    }

    fun updateTheme(isDark: Boolean) {
        viewModelScope.launch {
            settingsRepository.toggleTheme(isDark)
        }
    }

    fun signOut() {
        auth.signOut()
        showSnackbar(R.string.logout_success)
    }

    fun updateLanguage(code: String) {
        LocaleHelper.saveLanguage(context, code)

        viewModelScope.launch {
            settingsRepository.updateLanguage(code)
            _state.update { it.copy(currentLanguage = code) }
        }
    }
}