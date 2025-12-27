package com.example.movieapp.domain.usecase.favorites

import com.example.movieapp.domain.repository.FavoriteRepository
import javax.inject.Inject

class RemoveMovieFromListUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(listId: Long, movieId: Int) {
        repository.removeMovieFromList(listId, movieId)
    }
}