package com.example.movieapp.ui.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.movieapp.R
import com.example.movieapp.common.BaseViewModel
import com.example.movieapp.common.Resource
import com.example.movieapp.common.UiEvent
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.usecase.detail.GetMovieDetailUseCase
import com.example.movieapp.domain.usecase.favorites.CreateFavoriteListUseCase
import com.example.movieapp.domain.usecase.favorites.GetAllFavoriteMovieIdsUseCase
import com.example.movieapp.domain.usecase.favorites.GetFavoriteListsForMovieUseCase
import com.example.movieapp.domain.usecase.favorites.ToggleMovieInListUseCase
import com.example.movieapp.ui.feature.search.FavoriteListUiModel
import com.example.movieapp.ui.navigation.screen.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getFavoriteListsForMovieUseCase: GetFavoriteListsForMovieUseCase,
    private val createFavoriteListUseCase: CreateFavoriteListUseCase,
    private val toggleMovieInListUseCase: ToggleMovieInListUseCase,
    private val getAllFavoriteMovieIdsUseCase: GetAllFavoriteMovieIdsUseCase
) : BaseViewModel() {

    private val args = savedStateHandle.toRoute<Screen.Detail>()
    private val movieId = args.movieId

    private val _movieState = MutableStateFlow<Resource<Movie>>(Resource.Loading())
    val movieState = _movieState.asStateFlow()

    private val _favoriteListsState = MutableStateFlow<List<FavoriteListUiModel>>(emptyList())
    val favoriteListsState = _favoriteListsState.asStateFlow()

    val isFavorite = getAllFavoriteMovieIdsUseCase().map { it.contains(movieId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        fetchMovieDetail()
    }

    private fun fetchMovieDetail() {
        viewModelScope.launch {
            _movieState.value = Resource.Loading()
            val result = getMovieDetailUseCase(movieId)

            _movieState.value = result
        }
    }

    fun fetchLists() {
        viewModelScope.launch {
            getFavoriteListsForMovieUseCase(movieId).collect { lists ->
                _favoriteListsState.value = lists
            }
        }
    }

    fun createNewList(listName: String) {
        if (listName.isBlank()) return
        viewModelScope.launch {
            createFavoriteListUseCase(listName)
        }
    }

    fun toggleMovieInList(listId: Long, isChecked: Boolean) {
        val currentState = _movieState.value

        if (currentState is Resource.Success) {
            val movie = currentState.data

            _favoriteListsState.update { currentList ->
                currentList.map { if (it.id == listId) it.copy(isMovieInList = isChecked) else it }
            }

            viewModelScope.launch {
                movie?.let {
                    toggleMovieInListUseCase(listId, movie, isChecked)
                }

                val messageResId = if (isChecked) R.string.movie_added else R.string.movie_removed
                sendEvent(UiEvent.ShowSnackbar(messageResId))
            }
        }
    }
}