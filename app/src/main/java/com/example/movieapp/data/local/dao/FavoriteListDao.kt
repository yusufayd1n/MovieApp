package com.example.movieapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieapp.data.local.entity.FavoriteListEntity
import com.example.movieapp.data.local.entity.MovieListCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteList(favoriteList: FavoriteListEntity): Long

    @Query("SELECT * FROM favorite_lists ORDER BY listId DESC")
    fun getAllFavoriteLists(): Flow<List<FavoriteListEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMovieToList(crossRef: MovieListCrossRef)

    @Query("DELETE FROM movie_list_cross_ref WHERE listId = :listId AND movieId = :movieId")
    suspend fun removeMovieFromList(listId: Long, movieId: Int)

    @Query("SELECT listId FROM movie_list_cross_ref WHERE movieId = :movieId")
    fun getListIdsForMovie(movieId: Int): Flow<List<Long>>

    @Query("SELECT movieId FROM movie_list_cross_ref WHERE listId = :listId")
    fun getMovieIdsInList(listId: Long): Flow<List<Int>>

    @Query("SELECT DISTINCT movieId FROM movie_list_cross_ref")
    fun getAllFavoriteMovieIdsFlow(): Flow<List<Int>>
}