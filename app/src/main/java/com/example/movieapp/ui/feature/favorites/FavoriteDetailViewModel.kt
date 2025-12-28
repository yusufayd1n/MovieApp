package com.example.movieapp.ui.feature.favorites

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.movieapp.R
import com.example.movieapp.common.BaseViewModel
import com.example.movieapp.common.UiEvent
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.usecase.favorites.GetAllFavoriteListsUseCase
import com.example.movieapp.domain.usecase.favorites.GetMoviesByListIdUseCase
import com.example.movieapp.domain.usecase.favorites.RemoveMovieFromListUseCase
import com.example.movieapp.domain.usecase.favorites.ToggleMovieInListUseCase
import com.example.movieapp.ui.navigation.screen.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMoviesByListIdUseCase: GetMoviesByListIdUseCase,
    private val removeMovieFromListUseCase: RemoveMovieFromListUseCase,
    private val toggleMovieInListUseCase: ToggleMovieInListUseCase,
    private val getAllFavoriteListsUseCase: GetAllFavoriteListsUseCase
) : BaseViewModel() {

    private val routeArgs = savedStateHandle.toRoute<Screen.FavoriteListDetail>()
    val listId = routeArgs.listId

    val movies = getMoviesByListIdUseCase(listId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allLists = getAllFavoriteListsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _movieToDelete = MutableStateFlow<Movie?>(null)
    val movieToDelete = _movieToDelete.asStateFlow()

    private val _movieToMove = MutableStateFlow<Movie?>(null)
    val movieToMove = _movieToMove.asStateFlow()

    fun onRemoveClick(movie: Movie) {
        _movieToDelete.value = movie
    }

    fun onDismissDialog() {
        _movieToDelete.value = null
    }

    fun onMoveClick(movie: Movie) {
        _movieToMove.value = movie
    }

    fun onDismissBottomSheet() {
        _movieToMove.value = null
    }

    fun onConfirmDelete() {
        val movie = _movieToDelete.value ?: return

        viewModelScope.launch {
            removeMovieFromListUseCase(listId, movie.id)
            _movieToDelete.value = null

            sendEvent(UiEvent.ShowSnackbar(R.string.movie_removed))
        }
    }

    fun onTargetListSelected(targetListId: Long) {
        val movie = _movieToMove.value ?: return

        viewModelScope.launch {
            toggleMovieInListUseCase(
                listId = targetListId,
                movie = movie,
                isChecked = true
            )

            _movieToMove.value = null

            sendEvent(UiEvent.ShowSnackbar(R.string.movie_added))
        }
    }
}
