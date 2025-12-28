package com.example.movieapp.common

import com.example.movieapp.ui.navigation.screen.Screen

sealed interface UiEvent {
    data class ShowSnackbar(
        val messageResId: Int,
        val remoteMessage: String? = null
    ) : UiEvent

    data class Navigate(val screen: Screen) : UiEvent

    data object PopBackStack : UiEvent
}