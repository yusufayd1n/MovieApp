package com.example.movieapp.domain.usecase.favorites

import com.example.movieapp.domain.repository.FavoriteRepository
import javax.inject.Inject

class CreateFavoriteListUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(listName: String) {
        if (listName.isNotBlank()) {
            repository.createList(listName)
        }
    }
}