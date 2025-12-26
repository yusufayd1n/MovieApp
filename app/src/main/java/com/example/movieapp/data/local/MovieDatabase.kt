package com.example.movieapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.movieapp.data.local.dao.SearchHistoryDao
import com.example.movieapp.data.local.entity.SearchHistoryEntity

@Database(entities = [SearchHistoryEntity::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}