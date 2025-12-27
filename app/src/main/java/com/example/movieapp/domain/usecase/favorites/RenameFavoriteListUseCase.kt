package com.example.movieapp.domain.usecase.favorites

import com.example.movieapp.domain.repository.FavoriteRepository
import javax.inject.Inject

class RenameFavoriteListUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(listId: Long, newName: String) {
        if (newName.isNotBlank()) {
            repository.renameList(listId, newName)
        }
    }
}