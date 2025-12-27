package com.example.movieapp.domain.usecase.favorites

import com.example.movieapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllFavoriteMovieIdsUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<Int>> {
        return repository.getAllFavoriteMovieIdsFlow()
    }
}