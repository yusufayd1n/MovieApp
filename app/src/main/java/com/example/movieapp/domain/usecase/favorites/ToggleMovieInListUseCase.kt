package com.example.movieapp.domain.usecase.favorites

import com.example.movieapp.domain.repository.FavoriteRepository
import javax.inject.Inject

class ToggleMovieInListUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(listId: Long, movieId: Int, isChecked: Boolean) {
        if (isChecked) {
            repository.addMovieToList(listId, movieId)
        } else {
            repository.removeMovieFromList(listId, movieId)
        }
    }
}