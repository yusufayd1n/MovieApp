package com.example.movieapp.common

sealed interface UiEvent {
    data class ShowSnackbar(val messageResId: Int) : UiEvent
    //data class Navigate(val route: String) : UiEvent
    //data object PopBackStack : UiEvent
}