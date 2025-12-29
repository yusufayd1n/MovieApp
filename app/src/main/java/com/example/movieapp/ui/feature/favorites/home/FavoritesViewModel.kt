package com.example.movieapp.ui.feature.favorites.home

import androidx.lifecycle.viewModelScope
import com.example.movieapp.R
import com.example.movieapp.common.ui.BaseViewModel
import com.example.movieapp.common.ui.UiEvent
import com.example.movieapp.data.local.entity.FavoriteListEntity
import com.example.movieapp.domain.usecase.favorites.CreateFavoriteListUseCase
import com.example.movieapp.domain.usecase.favorites.DeleteFavoriteListUseCase
import com.example.movieapp.domain.usecase.favorites.GetAllFavoriteListsUseCase
import com.example.movieapp.domain.usecase.favorites.RenameFavoriteListUseCase
import com.example.movieapp.ui.navigation.screen.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getAllListsUseCase: GetAllFavoriteListsUseCase,
    private val createListUseCase: CreateFavoriteListUseCase,
    private val deleteListUseCase: DeleteFavoriteListUseCase,
    private val renameListUseCase: RenameFavoriteListUseCase
) : BaseViewModel() {

    // Tek bir State Flow
    private val _state = MutableStateFlow(FavoritesState())
    val state = _state.asStateFlow()

    init {
        observeLists()
    }

    private fun observeLists() {
        viewModelScope.launch {
            getAllListsUseCase().collect { lists ->
                _state.update { it.copy(favoriteLists = lists) }
            }
        }
    }

    fun onAddListClicked() {
        _state.update { it.copy(dialogState = FavoritesDialogState.Create) }
    }

    fun onDeleteListClicked(list: FavoriteListEntity) {
        _state.update { it.copy(dialogState = FavoritesDialogState.Delete(list)) }
    }

    fun onRenameListClicked(list: FavoriteListEntity) {
        _state.update { it.copy(dialogState = FavoritesDialogState.Rename(list)) }
    }

    fun onDialogDismiss() {
        _state.update { it.copy(dialogState = FavoritesDialogState.None) }
    }

    fun createList(name: String) {
        viewModelScope.launch {
            createListUseCase(name)
            onDialogDismiss()
            showSnackbar(R.string.list_created_message)
        }
    }

    fun deleteList(listId: Long) {
        viewModelScope.launch {
            deleteListUseCase(listId)
            onDialogDismiss()
            showSnackbar(R.string.list_deleted_message)
        }
    }

    fun renameList(listId: Long, newName: String) {
        viewModelScope.launch {
            renameListUseCase(listId, newName)
            onDialogDismiss()
            showSnackbar(R.string.list_renamed_message)
        }
    }

    fun onListClicked(listId: Long) {
        sendEvent(UiEvent.Navigate(Screen.FavoriteListDetail(listId)))
    }
}

sealed interface FavoritesDialogState {
    data object None : FavoritesDialogState
    data object Create : FavoritesDialogState
    data class Rename(val list: FavoriteListEntity) : FavoritesDialogState
    data class Delete(val list: FavoriteListEntity) : FavoritesDialogState
}

