package com.example.movieapp.ui.feature.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.movieapp.R
import com.example.movieapp.common.BaseViewModel
import com.example.movieapp.common.UiEvent
import com.example.movieapp.data.local.entity.SearchHistoryEntity
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.usecase.favorites.CreateFavoriteListUseCase
import com.example.movieapp.domain.usecase.favorites.GetAllFavoriteMovieIdsUseCase
import com.example.movieapp.domain.usecase.favorites.GetFavoriteListsForMovieUseCase
import com.example.movieapp.domain.usecase.favorites.ToggleMovieInListUseCase
import com.example.movieapp.domain.usecase.search.SearchHistoryUseCase
import com.example.movieapp.domain.usecase.search.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val searchHistoryUseCase: SearchHistoryUseCase,
    private val getFavoriteListsForMovieUseCase: GetFavoriteListsForMovieUseCase,
    private val createFavoriteListUseCase: CreateFavoriteListUseCase,
    private val toggleMovieInListUseCase: ToggleMovieInListUseCase,
    private val getAllFavoriteMovieIdsUseCase: GetAllFavoriteMovieIdsUseCase
) : BaseViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    var hasSearched by mutableStateOf(false)
        private set

    var filterState by mutableStateOf(FilterState())
        private set

    private val _moviesState = MutableStateFlow<PagingData<Movie>>(PagingData.empty())
    val moviesState: StateFlow<PagingData<Movie>> = _moviesState.asStateFlow()

    private val _favoriteListsState = MutableStateFlow<List<FavoriteListUiModel>>(emptyList())
    val favoriteListsState = _favoriteListsState.asStateFlow()

    val searchHistory: StateFlow<List<SearchHistoryEntity>> = searchHistoryUseCase.getHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val likedMovieIds = getAllFavoriteMovieIdsUseCase()
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    private var currentSelectedMovieId: Int? = null

    fun onQueryChange(newQuery: String) {
        searchQuery = newQuery
    }

    fun updateFilter(newState: FilterState) {
        filterState = newState
        if (hasSearched) {
            searchMovies()
        }
    }

    fun searchMovies() {
        if (searchQuery.isBlank()) {
            sendEvent(UiEvent.ShowSnackbar(R.string.search_validation_error))
            return
        }
        hasSearched = true

        viewModelScope.launch {
            searchHistoryUseCase.addHistory(searchQuery)

            searchMoviesUseCase(
                query = searchQuery,
                year = filterState.selectedYear.ifBlank { null },
                sortOption = filterState.sortOption
            )
                .cachedIn(viewModelScope)
                .map { pagingData ->
                    pagingData.filter { movie ->
                        val isVoteEnough = movie.voteAverage >= filterState.minVote
                        val isGenreCorrect = filterState.selectedGenreId?.let { id ->
                            movie.genreIds.contains(id)
                        } ?: true
                        isVoteEnough && isGenreCorrect
                    }
                }
                .collect { pagingData ->
                    _moviesState.value = pagingData
                }
        }
    }

    fun onHistoryClick(query: String) {
        searchQuery = query
        searchMovies()
    }

    fun fetchListsForMovie(movieId: Int) {
        currentSelectedMovieId = movieId
        viewModelScope.launch {
            getFavoriteListsForMovieUseCase(movieId).collect { uiModelList ->
                _favoriteListsState.value = uiModelList
            }
        }
    }

    fun createNewList(listName: String) {
        viewModelScope.launch {
            createFavoriteListUseCase(listName)
        }
    }

    fun toggleMovieInList(listId: Long, isChecked: Boolean, movie: Movie) {
        viewModelScope.launch {
            toggleMovieInListUseCase(listId, movie, isChecked)

            val messageResId = if (isChecked) R.string.movie_added else R.string.movie_removed
            sendEvent(UiEvent.ShowSnackbar(messageResId))
        }
    }
}