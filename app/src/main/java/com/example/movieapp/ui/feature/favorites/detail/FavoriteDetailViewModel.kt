package com.example.movieapp.ui.feature.favorites.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.movieapp.R
import com.example.movieapp.common.ui.BaseViewModel
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.usecase.favorites.GetAllFavoriteListsUseCase
import com.example.movieapp.domain.usecase.favorites.GetMoviesByListIdUseCase
import com.example.movieapp.domain.usecase.favorites.RemoveMovieFromListUseCase
import com.example.movieapp.domain.usecase.favorites.ToggleMovieInListUseCase
import com.example.movieapp.ui.navigation.screen.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _state = MutableStateFlow(FavoriteDetailState())
    val state = _state.asStateFlow()

    init {
        observeMovies()
        observeAllLists()
    }

    private fun observeMovies() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getMoviesByListIdUseCase(listId).collect { movieList ->
                _state.update {
                    it.copy(
                        movies = movieList,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun observeAllLists() {
        viewModelScope.launch {
            getAllFavoriteListsUseCase().collect { lists ->
                _state.update { it.copy(allLists = lists) }
            }
        }
    }

    fun onRemoveClick(movie: Movie) {
        _state.update { it.copy(movieToDelete = movie) }
    }

    fun onDismissDialog() {
        _state.update { it.copy(movieToDelete = null) }
    }

    fun onMoveClick(movie: Movie) {
        _state.update { it.copy(movieToMove = movie) }
    }

    fun onDismissBottomSheet() {
        _state.update { it.copy(movieToMove = null) }
    }

    fun onConfirmDelete() {
        val movie = _state.value.movieToDelete ?: return

        viewModelScope.launch {
            removeMovieFromListUseCase(listId, movie.id)

            _state.update { it.copy(movieToDelete = null) }

            showSnackbar(R.string.movie_removed)
        }
    }

    fun onTargetListSelected(targetListId: Long) {
        val movie = _state.value.movieToMove ?: return

        viewModelScope.launch {
            toggleMovieInListUseCase(
                listId = targetListId,
                movie = movie,
                isChecked = true
            )

            _state.update { it.copy(movieToMove = null) }

            showSnackbar(R.string.movie_added)
        }
    }
}
