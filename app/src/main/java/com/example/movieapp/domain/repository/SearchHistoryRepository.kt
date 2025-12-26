package com.example.movieapp.domain.repository

import com.example.movieapp.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>
    suspend fun addSearchQuery(query: String)
}