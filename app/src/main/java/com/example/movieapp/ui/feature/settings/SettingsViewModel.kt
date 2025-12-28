package com.example.movieapp.ui.feature.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.movieapp.R
import com.example.movieapp.common.BaseViewModel
import com.example.movieapp.common.LocaleHelper
import com.example.movieapp.data.repository.SettingsRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    val currentUserState: StateFlow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = auth.currentUser
    )

    val isDarkTheme = settingsRepository.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _currentLanguage = MutableStateFlow(LocaleHelper.getLanguage(context))
    val currentLanguage = _currentLanguage.asStateFlow()

    var showChangePasswordDialog by mutableStateOf(false)
        private set

    fun showPasswordDialog() { showChangePasswordDialog = true }
    fun hidePasswordDialog() { showChangePasswordDialog = false }

    fun changePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser
        val email = user?.email

        if (user == null || email == null) return

        if (currentPassword.isBlank() || newPassword.isBlank()) {
            showSnackbar(R.string.fill_all_fields_error)
            return
        }

        if (newPassword.length < 6) {
            showSnackbar(messageResId = R.string.error_unknown)
            return
        }

        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        hidePasswordDialog()
                        showSnackbar(messageResId = R.string.password_reset_succes)
                    }
                    .addOnFailureListener { e ->
                        showSnackbar(messageResId = R.string.error_unknown, remoteMessage = e.localizedMessage)
                    }
            }
            .addOnFailureListener { e ->
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
            _currentLanguage.value = code
        }
    }
}