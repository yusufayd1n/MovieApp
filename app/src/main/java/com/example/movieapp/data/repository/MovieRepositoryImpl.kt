package com.example.movieapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.movieapp.common.utl.Resource
import com.example.movieapp.data.mapper.toMovie
import com.example.movieapp.data.paging.MoviePagingSource
import com.example.movieapp.data.remote.TmdbApi
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.MovieRepository
import com.example.movieapp.ui.feature.search.SortOption
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApi
) : MovieRepository {

    override fun searchMovies(
        query: String,
        year: String?,
        sortOption: SortOption
    ): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MoviePagingSource(api = api, query = query, year = year, sortOption = sortOption)
            }
        ).flow
    }

    override suspend fun getMovieDetail(movieId: Int): Resource<Movie> {
        return try {
            val remoteDto = api.getMovieDetail(movieId)
            val movie = remoteDto.toMovie()
            Resource.Success(movie)
        } catch (e: Exception) {
            //TODO
            Resource.Error(e.localizedMessage)
        }
    }
}