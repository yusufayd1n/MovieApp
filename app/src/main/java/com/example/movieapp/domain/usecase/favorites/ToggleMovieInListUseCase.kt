package com.example.movieapp.domain.usecase.favorites

import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.FavoriteRepository
import javax.inject.Inject

class ToggleMovieInListUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(listId: Long, movie: Movie, isChecked: Boolean) {
        if (isChecked) {
            repository.addMovieToList(listId, movie)
        } else {
            repository.removeMovieFromList(listId, movie.id)
        }
    }
}