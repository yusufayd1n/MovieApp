package com.example.movieapp.domain.usecase.detail

import com.example.movieapp.common.Resource
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Resource<Movie>{
        return repository.getMovieDetail(movieId) 
    }
}