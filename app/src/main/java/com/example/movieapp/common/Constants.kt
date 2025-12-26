package com.example.movieapp.common

import com.example.movieapp.R

object Constants {
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/"
}

object GenreConstants {
    val genreMap = mapOf(
        28 to R.string.genre_action,
        12 to R.string.genre_adventure,
        16 to R.string.genre_animation,
        35 to R.string.genre_comedy,
        80 to R.string.genre_crime,
        99 to R.string.genre_documentary,
        18 to R.string.genre_drama,
        10751 to R.string.genre_family,
        14 to R.string.genre_fantasy,
        36 to R.string.genre_history,
        27 to R.string.genre_horror,
        10402 to R.string.genre_music,
        9648 to R.string.genre_mystery,
        10749 to R.string.genre_romance,
        878 to R.string.genre_scifi,
        10770 to R.string.genre_tv_movie,
        53 to R.string.genre_thriller,
        10752 to R.string.genre_war,
        37 to R.string.genre_western
    )
}