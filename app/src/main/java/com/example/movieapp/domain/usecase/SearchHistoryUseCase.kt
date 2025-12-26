package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class SearchHistoryUseCase @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) {
    fun getHistory() = searchHistoryRepository.getSearchHistory()

    suspend fun addHistory(query: String) {
        if (query.isNotBlank()) {
            searchHistoryRepository.addSearchQuery(query)
        }
    }
}