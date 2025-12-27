package com.example.movieapp.ui.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.R
import com.example.movieapp.data.local.entity.FavoriteListEntity
import com.example.movieapp.domain.usecase.favorites.CreateFavoriteListUseCase
import com.example.movieapp.domain.usecase.favorites.DeleteFavoriteListUseCase
import com.example.movieapp.domain.usecase.favorites.GetAllFavoriteListsUseCase
import com.example.movieapp.domain.usecase.favorites.RenameFavoriteListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getAllListsUseCase: GetAllFavoriteListsUseCase,
    private val createListUseCase: CreateFavoriteListUseCase,
    private val deleteListUseCase: DeleteFavoriteListUseCase,
    private val renameListUseCase: RenameFavoriteListUseCase
) : ViewModel() {
    val favoriteLists = getAllListsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _dialogState = MutableStateFlow<FavoritesDialogState>(FavoritesDialogState.None)
    val dialogState = _dialogState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onAddListClicked() {
        _dialogState.value = FavoritesDialogState.Create
    }

    fun onDeleteListClicked(list: FavoriteListEntity) {
        _dialogState.value = FavoritesDialogState.Delete(list)
    }

    fun onRenameListClicked(list: FavoriteListEntity) {
        _dialogState.value = FavoritesDialogState.Rename(list)
    }

    fun onDialogDismiss() {
        _dialogState.value = FavoritesDialogState.None
    }

    fun createList(name: String) {
        viewModelScope.launch {
            createListUseCase(name)
            onDialogDismiss()
            _uiEvent.send(UiEvent.ShowSnackbar(R.string.list_created_message))
        }
    }

    fun deleteList(listId: Long) {
        viewModelScope.launch {
            deleteListUseCase(listId)
            onDialogDismiss()
            _uiEvent.send(UiEvent.ShowSnackbar(R.string.list_deleted_message))
        }
    }

    fun renameList(listId: Long, newName: String) {
        viewModelScope.launch {
            renameListUseCase(listId, newName)
            onDialogDismiss()
            _uiEvent.send(UiEvent.ShowSnackbar(R.string.list_renamed_message))
        }
    }
    sealed interface UiEvent {
        data class ShowSnackbar(val messageResId: Int) : UiEvent
    }
}


sealed interface FavoritesDialogState {
    data object None : FavoritesDialogState
    data object Create : FavoritesDialogState
    data class Rename(val list: FavoriteListEntity) : FavoritesDialogState
    data class Delete(val list: FavoriteListEntity) : FavoritesDialogState
}

