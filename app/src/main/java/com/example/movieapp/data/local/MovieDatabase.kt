package com.example.movieapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.movieapp.data.local.dao.FavoriteListDao
import com.example.movieapp.data.local.dao.SearchHistoryDao
import com.example.movieapp.data.local.entity.FavoriteListEntity
import com.example.movieapp.data.local.entity.MovieEntity
import com.example.movieapp.data.local.entity.MovieListCrossRef
import com.example.movieapp.data.local.entity.SearchHistoryEntity

@Database(
    entities = [
        SearchHistoryEntity::class,
        FavoriteListEntity::class,
        MovieListCrossRef::class,
        MovieEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun favoriteListDao(): FavoriteListDao
}