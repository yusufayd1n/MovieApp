package com.example.movieapp.ui.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.R
import com.example.movieapp.common.Resource
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.usecase.detail.GetMovieDetailUseCase
import com.example.movieapp.domain.usecase.favorites.CreateFavoriteListUseCase
import com.example.movieapp.domain.usecase.favorites.GetAllFavoriteMovieIdsUseCase
import com.example.movieapp.domain.usecase.favorites.GetFavoriteListsForMovieUseCase
import com.example.movieapp.domain.usecase.favorites.ToggleMovieInListUseCase
import com.example.movieapp.ui.feature.search.FavoriteListUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
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
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle["movieId"])

    private val _movieState = MutableStateFlow<Resource<Movie>>(Resource.Loading())
    val movieState = _movieState.asStateFlow()

    private val _favoriteListsState = MutableStateFlow<List<FavoriteListUiModel>>(emptyList())
    val favoriteListsState = _favoriteListsState.asStateFlow()

    val isFavorite = getAllFavoriteMovieIdsUseCase().map { it.contains(movieId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

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
        _favoriteListsState.update { currentList ->
            currentList.map { if (it.id == listId) it.copy(isMovieInList = isChecked) else it }
        }

        viewModelScope.launch {
            toggleMovieInListUseCase(listId, movieId, isChecked)

            val messageResId = if (isChecked) R.string.movie_added else R.string.movie_removed
            _uiEvent.send(UiEvent.ShowSnackbar(messageResId))
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val messageResId: Int) : UiEvent()
    }
}