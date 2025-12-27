package com.example.movieapp.data.repository

import com.example.movieapp.data.local.dao.FavoriteListDao
import com.example.movieapp.data.local.entity.FavoriteListEntity
import com.example.movieapp.data.local.entity.MovieListCrossRef
import com.example.movieapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val dao: FavoriteListDao
) : FavoriteRepository {

    override fun getAllLists(): Flow<List<FavoriteListEntity>> {
        return dao.getAllFavoriteLists()
    }

    override suspend fun createList(listName: String) {
        val newList = FavoriteListEntity(listName = listName)
        dao.insertFavoriteList(newList)
    }

    override suspend fun addMovieToList(listId: Long, movieId: Int) {
        val crossRef = MovieListCrossRef(listId = listId, movieId = movieId)
        dao.addMovieToList(crossRef)
    }

    override suspend fun removeMovieFromList(listId: Long, movieId: Int) {
        dao.removeMovieFromList(listId, movieId)
    }

    override fun getListIdsForMovie(movieId: Int): Flow<List<Long>> {
        return dao.getListIdsForMovie(movieId)
    }

    override fun getAllFavoriteMovieIdsFlow(): Flow<List<Int>> {
        return dao.getAllFavoriteMovieIdsFlow()
    }
}