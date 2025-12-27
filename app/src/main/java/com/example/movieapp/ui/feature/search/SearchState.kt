package com.example.movieapp.ui.feature.search

import com.example.movieapp.R

enum class SortOption(val titleResId: Int) {
    DEFAULT(R.string.sort_default),
    A_Z(R.string.sort_az),
    Z_A(R.string.sort_za),
    NEWEST(R.string.sort_date),
    RATING(R.string.sort_rating)
}

data class FilterState(
    val selectedYear: String = "",
    val minVote: Int = 0,
    val selectedGenreId: Int? = null,
    val sortOption: SortOption = SortOption.DEFAULT
)

data class FavoriteListUiModel(
    val id: Long,
    val name: String,
    val isMovieInList: Boolean
)