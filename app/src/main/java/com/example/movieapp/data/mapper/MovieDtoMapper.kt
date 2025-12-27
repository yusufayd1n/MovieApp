package com.example.movieapp.data.mapper

import com.example.movieapp.common.Constants
import com.example.movieapp.data.remote.model.MovieDto
import com.example.movieapp.domain.model.Movie

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title ?: "İsimsiz Film",
        overview = overview ?: "",
        posterPath = if (!posterPath.isNullOrEmpty()) "${Constants.IMAGE_BASE_URL}$posterPath" else null,
        releaseDate = releaseDate ?: "",
        voteAverage = voteAverage ?: 0.0,
        genreIds = genreIds ?: emptyList()
    )
}

fun MovieDto.toMovie(): Movie {
    return Movie(
        id = id,
        title = title.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = if (!posterPath.isNullOrEmpty()) "${Constants.IMAGE_BASE_URL}$posterPath" else null,
        releaseDate = releaseDate.orEmpty(),
        voteAverage = voteAverage ?: 0.0,
        genreIds = genreIds ?: genres?.map { it.id } ?: emptyList()
    )
}