package com.example.movieapp

import app.cash.turbine.test
import com.example.movieapp.R
import com.example.movieapp.common.ui.UiEvent
import com.example.movieapp.data.local.entity.FavoriteListEntity
import com.example.movieapp.domain.usecase.favorites.CreateFavoriteListUseCase
import com.example.movieapp.domain.usecase.favorites.DeleteFavoriteListUseCase
import com.example.movieapp.domain.usecase.favorites.GetAllFavoriteListsUseCase
import com.example.movieapp.domain.usecase.favorites.RenameFavoriteListUseCase
import com.example.movieapp.ui.feature.favorites.home.FavoritesDialogState
import com.example.movieapp.ui.feature.favorites.home.FavoritesViewModel
import com.example.movieapp.ui.navigation.screen.Screen
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var getAllListsUseCase: GetAllFavoriteListsUseCase
    @MockK
    private lateinit var createListUseCase: CreateFavoriteListUseCase
    @MockK
    private lateinit var deleteListUseCase: DeleteFavoriteListUseCase
    @MockK
    private lateinit var renameListUseCase: RenameFavoriteListUseCase

    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { getAllListsUseCase() } returns flowOf(emptyList())

        viewModel = FavoritesViewModel(
            getAllListsUseCase,
            createListUseCase,
            deleteListUseCase,
            renameListUseCase
        )
    }

    @Test
    fun `onAddListClicked updates state with dialogState Create`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.onAddListClicked()

            val newState = awaitItem()
            assertEquals(FavoritesDialogState.Create, newState.dialogState)
        }
    }

    @Test
    fun `onDeleteListClicked updates state with dialogState Delete`() = runTest {
        val list = FavoriteListEntity(listId = 1, listName = "My List")

        viewModel.state.test {
            awaitItem()

            viewModel.onDeleteListClicked(list)

            val newState = awaitItem()
            assertTrue(newState.dialogState is FavoritesDialogState.Delete)
            assertEquals(list, (newState.dialogState as FavoritesDialogState.Delete).list)
        }
    }

    @Test
    fun `createList calls useCase, resets dialogState and shows snackbar`() = runTest {
        val listName = "New List"
        coEvery { createListUseCase(listName) } just Runs

        viewModel.onAddListClicked()

        viewModel.state.test {
            val currentState = awaitItem()
            assertEquals(FavoritesDialogState.Create, currentState.dialogState)

            // When
            viewModel.createList(listName)

            val newState = awaitItem()
            assertEquals(FavoritesDialogState.None, newState.dialogState)
        }

        coVerify { createListUseCase(listName) }

        viewModel.uiEvent.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.ShowSnackbar)
            assertEquals(R.string.list_created_message, (event as UiEvent.ShowSnackbar).messageResId)
        }
    }

    @Test
    fun `deleteList calls useCase and shows snackbar`() = runTest {
        val listId = 1L
        coEvery { deleteListUseCase(listId) } just Runs

        viewModel.deleteList(listId)

        coVerify { deleteListUseCase(listId) }

        assertEquals(FavoritesDialogState.None, viewModel.state.value.dialogState)

        viewModel.uiEvent.test {
            val event = awaitItem()
            assertEquals(R.string.list_deleted_message, (event as UiEvent.ShowSnackbar).messageResId)
        }
    }

    @Test
    fun `onListClicked sends Navigate event`() = runTest {
        val listId = 5L

        viewModel.onListClicked(listId)

        viewModel.uiEvent.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.Navigate)
            assertEquals(Screen.FavoriteListDetail(listId), (event as UiEvent.Navigate).screen)
        }
    }
}