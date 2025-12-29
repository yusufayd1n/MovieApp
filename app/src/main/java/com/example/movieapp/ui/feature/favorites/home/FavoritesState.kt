package com.example.movieapp.ui.feature.favorites.home

import com.example.movieapp.data.local.entity.FavoriteListEntity

data class FavoritesState(
    val favoriteLists: List<FavoriteListEntity> = emptyList(),
    val dialogState: FavoritesDialogState = FavoritesDialogState.None
)