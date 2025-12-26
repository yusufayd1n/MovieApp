package com.example.movieapp.domain.usecase

import androidx.paging.PagingData
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.MovieRepository
import com.example.movieapp.ui.feature.search.SortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(query: String, year: String? = null, sortOption: SortOption): Flow<PagingData<Movie>> {
        if (query.isBlank()) {
            return emptyFlow()
        }
        return repository.searchMovies(query = query, year = year, sortOption = sortOption)
    }
}