package com.example.movieapp.ui.feature.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.movieapp.R
import com.example.movieapp.data.local.entity.SearchHistoryEntity
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.usecase.SearchHistoryUseCase
import com.example.movieapp.domain.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val searchHistoryUseCase: SearchHistoryUseCase
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    var hasSearched by mutableStateOf(false)
        private set

    var filterState by mutableStateOf(FilterState())
        private set

    private val _moviesState = MutableStateFlow<PagingData<Movie>>(PagingData.empty())
    val moviesState: StateFlow<PagingData<Movie>> = _moviesState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val searchHistory: StateFlow<List<SearchHistoryEntity>> = searchHistoryUseCase.getHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

    private fun sendEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val messageResId: Int) : UiEvent()
    }
}