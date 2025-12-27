package com.example.movieapp.domain.usecase.favorites

import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMoviesByListIdUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    operator fun invoke(listId: Long): Flow<List<Movie>> {
        return repository.getMoviesByListId(listId)
    }
}