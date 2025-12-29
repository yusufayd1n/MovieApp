package com.example.movieapp

import androidx.paging.PagingData
import app.cash.turbine.test
import com.example.movieapp.common.ui.UiEvent
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.usecase.favorites.CreateFavoriteListUseCase
import com.example.movieapp.domain.usecase.favorites.GetAllFavoriteMovieIdsUseCase
import com.example.movieapp.domain.usecase.favorites.GetFavoriteListsForMovieUseCase
import com.example.movieapp.domain.usecase.favorites.ToggleMovieInListUseCase
import com.example.movieapp.domain.usecase.search.SearchHistoryUseCase
import com.example.movieapp.domain.usecase.search.SearchMoviesUseCase
import com.example.movieapp.ui.feature.search.SearchViewModel
import com.example.movieapp.ui.navigation.screen.Screen
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var searchMoviesUseCase: SearchMoviesUseCase
    @MockK
    private lateinit var searchHistoryUseCase: SearchHistoryUseCase
    @MockK
    private lateinit var getFavoriteListsForMovieUseCase: GetFavoriteListsForMovieUseCase
    @MockK
    private lateinit var createFavoriteListUseCase: CreateFavoriteListUseCase
    @MockK
    private lateinit var toggleMovieInListUseCase: ToggleMovieInListUseCase
    @MockK
    private lateinit var getAllFavoriteMovieIdsUseCase: GetAllFavoriteMovieIdsUseCase

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { searchHistoryUseCase.getHistory() } returns flowOf(emptyList())
        every { getAllFavoriteMovieIdsUseCase() } returns flowOf(emptyList())

        viewModel = SearchViewModel(
            searchMoviesUseCase,
            searchHistoryUseCase,
            getFavoriteListsForMovieUseCase,
            createFavoriteListUseCase,
            toggleMovieInListUseCase,
            getAllFavoriteMovieIdsUseCase
        )
    }

    @Test
    fun `onQueryChange updates searchQuery state`() {
        val query = "Matrix"

        viewModel.onQueryChange(query)

        assertEquals(query, viewModel.searchQuery)
    }

    @Test
    fun `searchMovies with empty query shows error snackbar`() = runTest {
        viewModel.onQueryChange("")

        viewModel.searchMovies()

        viewModel.uiEvent.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.ShowSnackbar)
            assertEquals(R.string.search_validation_error, (event as UiEvent.ShowSnackbar).messageResId)
        }
        
        coVerify(exactly = 0) { searchMoviesUseCase(any(), any(), any()) }
    }

    @Test
    fun `searchMovies with valid query calls useCase and updates state`() = runTest {
        val query = "Batman"
        viewModel.onQueryChange(query)
        
        coEvery { searchHistoryUseCase.addHistory(query) } just Runs
        every { searchMoviesUseCase(query, null, any()) } returns flowOf(PagingData.empty())

        viewModel.searchMovies()

        assertTrue(viewModel.hasSearched)
        coVerify { searchHistoryUseCase.addHistory(query) }
        coVerify { searchMoviesUseCase(query, null, any()) }
    }

    @Test
    fun `onMovieClicked sends Navigate event`() = runTest {
        val movieId = 123

        viewModel.onMovieClicked(movieId)

        viewModel.uiEvent.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.Navigate)
            assertEquals(Screen.Detail(movieId), (event as UiEvent.Navigate).screen)
        }
    }

    @Test
    fun `toggleMovieInList adds movie and shows snackbar`() = runTest {
        val listId = 1L
        val isChecked = true
        val movie = mockk<Movie>()

        coEvery { toggleMovieInListUseCase(listId, movie, isChecked) } just Runs

        viewModel.toggleMovieInList(listId, isChecked, movie)

        coVerify { toggleMovieInListUseCase(listId, movie, isChecked) }
        
        viewModel.uiEvent.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.ShowSnackbar)
            assertEquals(R.string.movie_added, (event as UiEvent.ShowSnackbar).messageResId)
        }
    }
    
    @Test
    fun `toggleMovieInList removes movie and shows snackbar`() = runTest {
        val listId = 1L
        val isChecked = false
        val movie = mockk<Movie>()

        coEvery { toggleMovieInListUseCase(listId, movie, isChecked) } just Runs

        viewModel.toggleMovieInList(listId, isChecked, movie)

        viewModel.uiEvent.test {
            val event = awaitItem()
            assertEquals(R.string.movie_removed, (event as UiEvent.ShowSnackbar).messageResId)
        }
    }
}