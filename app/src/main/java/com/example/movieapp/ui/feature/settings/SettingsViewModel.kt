package com.example.movieapp.ui.feature.settings

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.movieapp.R
import com.example.movieapp.common.BaseViewModel
import com.example.movieapp.common.LocaleHelper
import com.example.movieapp.data.repository.SettingsRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    val currentUser = auth.currentUser

    val isDarkTheme = settingsRepository.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _currentLanguage = MutableStateFlow(LocaleHelper.getLanguage(context))
    val currentLanguage = _currentLanguage.asStateFlow()

    fun updateTheme(isDark: Boolean) {
        viewModelScope.launch {
            settingsRepository.toggleTheme(isDark)
        }
    }

    fun signOut() {
        auth.signOut()
        showSnackbar(R.string.logout_success)
    }

    fun sendPasswordResetEmail() {
        val email = currentUser?.email ?: return

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                showSnackbar(R.string.password_reset_sent)
            }
            .addOnFailureListener { exception ->
                showSnackbar(
                    messageResId = R.string.error_unknown,
                    remoteMessage = exception.localizedMessage
                )
            }
    }

    fun updateLanguage(code: String) {
        LocaleHelper.saveLanguage(context, code)

        viewModelScope.launch {
            settingsRepository.updateLanguage(code)
            _currentLanguage.value = code
        }
    }
}