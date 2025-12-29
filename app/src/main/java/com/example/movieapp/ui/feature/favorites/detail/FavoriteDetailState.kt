package com.example.movieapp.ui.feature.favorites.detail

import com.example.movieapp.data.local.entity.FavoriteListEntity
import com.example.movieapp.domain.model.Movie

data class FavoriteDetailState(
    val movies: List<Movie> = emptyList(),
    val allLists: List<FavoriteListEntity> = emptyList(),
    val movieToDelete: Movie? = null,
    val movieToMove: Movie? = null,
    val isLoading: Boolean = false
)