package com.example.movieapp.data.repository

import com.example.movieapp.data.local.dao.FavoriteListDao
import com.example.movieapp.data.local.entity.FavoriteListEntity
import com.example.movieapp.data.local.entity.MovieListCrossRef
import com.example.movieapp.data.mapper.toDomain
import com.example.movieapp.data.mapper.toEntity
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override suspend fun deleteList(listId: Long) {
        dao.deleteList(listId)
    }

    override suspend fun renameList(listId: Long, newName: String) {
        dao.updateListName(listId, newName)
    }

    override suspend fun addMovieToList(listId: Long, movie: Movie) {
        dao.insertMovie(movie.toEntity())

        val crossRef = MovieListCrossRef(listId = listId, movieId = movie.id)
        dao.addMovieToList(crossRef)
    }

    override suspend fun removeMovieFromList(listId: Long, movieId: Int) {
        dao.removeMovieFromList(listId, movieId)
    }

    override fun getMoviesByListId(listId: Long): Flow<List<Movie>> {
        return dao.getMoviesByListId(listId).map { entityList ->
            entityList.map { it.toDomain() }
        }
    }

    override fun getListIdsForMovie(movieId: Int): Flow<List<Long>> {
        return dao.getListIdsForMovie(movieId)
    }

    override fun getAllFavoriteMovieIdsFlow(): Flow<List<Int>> {
        return dao.getAllFavoriteMovieIdsFlow()
    }
}