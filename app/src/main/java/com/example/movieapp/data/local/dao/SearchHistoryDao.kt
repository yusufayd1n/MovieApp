package com.example.movieapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieapp.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(searchHistory: SearchHistoryEntity)

    @Query("SELECT COUNT(*) FROM search_history")
    suspend fun getCount(): Int

    @Query("DELETE FROM search_history WHERE query IN (SELECT query FROM search_history ORDER BY timestamp ASC LIMIT 1)")
    suspend fun deleteOldest()
}