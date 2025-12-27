package com.example.movieapp.domain.repository

import com.example.movieapp.data.local.entity.FavoriteListEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllLists(): Flow<List<FavoriteListEntity>>
    suspend fun createList(listName: String)
    suspend fun addMovieToList(listId: Long, movieId: Int)
    suspend fun removeMovieFromList(listId: Long, movieId: Int)
    fun getListIdsForMovie(movieId: Int): Flow<List<Long>>
    fun getAllFavoriteMovieIdsFlow(): Flow<List<Int>>
}