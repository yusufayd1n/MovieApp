package com.example.movieapp.domain.usecase.favorites

import com.example.movieapp.domain.repository.FavoriteRepository
import com.example.movieapp.ui.feature.search.FavoriteListUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFavoriteListsForMovieUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    operator fun invoke(movieId: Int): Flow<List<FavoriteListUiModel>> {
        val allListsFlow = repository.getAllLists()
        val movieInListsFlow = repository.getListIdsForMovie(movieId)

        return combine(allListsFlow, movieInListsFlow) { allLists, movieInListIds ->
            allLists.map { list ->
                FavoriteListUiModel(
                    id = list.listId,
                    name = list.listName,
                    isMovieInList = movieInListIds.contains(list.listId)
                )
            }
        }
    }
}