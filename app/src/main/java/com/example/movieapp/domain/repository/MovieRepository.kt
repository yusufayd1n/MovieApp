package com.example.movieapp.domain.repository

import androidx.paging.PagingData
import com.example.movieapp.common.utl.Resource
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.ui.feature.search.SortOption
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun searchMovies(query: String, year: String?, sortOption: SortOption): Flow<PagingData<Movie>>
    suspend fun getMovieDetail(movieId: Int): Resource<Movie>
}