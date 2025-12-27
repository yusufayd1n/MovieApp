package com.example.movieapp.data.mapper

import com.example.movieapp.common.Constants
import com.example.movieapp.data.local.entity.MovieEntity
import com.example.movieapp.data.remote.model.MovieDto
import com.example.movieapp.domain.model.Movie

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title ?: "İsimsiz Film",
        overview = overview.orEmpty(),
        posterPath = if (!posterPath.isNullOrEmpty()) "${Constants.IMAGE_BASE_URL}$posterPath" else null,
        releaseDate = releaseDate .orEmpty(),
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

fun Movie.toEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage
    )
}

fun MovieEntity.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate.orEmpty(),
        voteAverage = voteAverage,
        genreIds = emptyList()
    )
}