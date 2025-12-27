package com.example.movieapp.di

import com.example.movieapp.data.repository.FavoriteRepositoryImpl
import com.example.movieapp.data.repository.MovieRepositoryImpl
import com.example.movieapp.data.repository.SearchHistoryRepositoryImpl
import com.example.movieapp.domain.repository.FavoriteRepository
import com.example.movieapp.domain.repository.MovieRepository
import com.example.movieapp.domain.repository.SearchHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository

    @Binds
    abstract fun bindSearchHistoryRepository(
        searchHistoryRepositoryImpl: SearchHistoryRepositoryImpl
    ): SearchHistoryRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImpl: FavoriteRepositoryImpl
    ): FavoriteRepository
}