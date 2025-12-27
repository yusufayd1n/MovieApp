package com.example.movieapp.domain.repository

import com.example.movieapp.data.local.entity.FavoriteListEntity
import com.example.movieapp.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllLists(): Flow<List<FavoriteListEntity>>
    suspend fun createList(listName: String)
    suspend fun deleteList(listId: Long)
    suspend fun renameList(listId: Long, newName: String)
    suspend fun addMovieToList(listId: Long, movie: Movie)
    suspend fun removeMovieFromList(listId: Long, movieId: Int)
    fun getMoviesByListId(listId: Long): Flow<List<Movie>>
    fun getListIdsForMovie(movieId: Int): Flow<List<Long>>
    fun getAllFavoriteMovieIdsFlow(): Flow<List<Int>>

}