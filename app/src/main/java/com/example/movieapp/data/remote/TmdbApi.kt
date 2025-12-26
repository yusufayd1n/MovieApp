package com.example.movieapp.data.remote

import com.example.movieapp.data.remote.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "tr-TR",
        @Query("primary_release_year") year: String? = null
    ): MovieResponse
}