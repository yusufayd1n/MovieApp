package com.example.movieapp.data.repository

import com.example.movieapp.data.local.dao.SearchHistoryDao
import com.example.movieapp.data.local.entity.SearchHistoryEntity
import com.example.movieapp.domain.repository.SearchHistoryRepository
import javax.inject.Inject

const val SEARCH_STRING_LIMIT = 10

class SearchHistoryRepositoryImpl @Inject constructor(
    private val dao: SearchHistoryDao
) : SearchHistoryRepository {

    override fun getSearchHistory() = dao.getSearchHistory()

    override suspend fun addSearchQuery(query: String) {
        dao.insertSearch(SearchHistoryEntity(query = query))

        if (dao.getCount() > SEARCH_STRING_LIMIT) {
            dao.deleteOldest()
        }
    }
}