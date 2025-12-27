package com.example.movieapp.ui.navigation.screen

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Search : Screen()

    @Serializable
    data object Favorites : Screen()

    @Serializable
    data class FavoriteListDetail(val listId: Long) : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data class Detail(val movieId: Int) : Screen()
}