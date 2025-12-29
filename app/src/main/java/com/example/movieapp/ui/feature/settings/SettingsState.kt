package com.example.movieapp.ui.feature.settings

import com.google.firebase.auth.FirebaseUser

data class SettingsState(
    val isDarkTheme: Boolean = false,
    val currentLanguage: String = "tr",
    val currentUser: FirebaseUser? = null,
    val isPasswordDialogVisible: Boolean = false,
    val isLoading: Boolean = false
)