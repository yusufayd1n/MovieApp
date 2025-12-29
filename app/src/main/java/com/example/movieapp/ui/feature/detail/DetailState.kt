package com.example.movieapp.ui.feature.detail

import com.example.movieapp.common.utl.Resource
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.ui.feature.search.FavoriteListUiModel

data class DetailState(
    val movieState: Resource<Movie> = Resource.Loading(),
    val favoriteLists: List<FavoriteListUiModel> = emptyList(),
    val isFavorite: Boolean = false
)