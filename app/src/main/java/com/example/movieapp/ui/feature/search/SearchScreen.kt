package com.example.movieapp.ui.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.movieapp.R
import com.example.movieapp.common.ui.ObserveAsEvents
import com.example.movieapp.common.ui.UiEvent
import com.example.movieapp.data.local.entity.SearchHistoryEntity
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.ui.components.FilterBottomSheet
import com.example.movieapp.ui.components.MovieCard
import com.example.movieapp.ui.navigation.screen.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val movies = viewModel.moviesState.collectAsLazyPagingItems()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val favoriteLists by viewModel.favoriteListsState.collectAsState()
    val likedMovieIds by viewModel.likedMovieIds.collectAsState()

    val mainSnackbarHostState = remember { SnackbarHostState() }
    val sheetSnackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var active by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedMovieForFavorites by remember { mutableStateOf<Movie?>(null) }
    val interactionSource = remember { MutableInteractionSource() }

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is UiEvent.ShowSnackbar -> {
                scope.launch {
                    val message = event.remoteMessage ?: context.getString(event.messageResId)
                    if (selectedMovieForFavorites != null) {
                        sheetSnackbarHostState.showSnackbar(
                            message = message,
                            withDismissAction = true,
                            duration = SnackbarDuration.Short
                        )
                    } else {
                        mainSnackbarHostState.showSnackbar(
                            message = message,
                            withDismissAction = true,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }

            is UiEvent.Navigate -> {
                onNavigate(event.screen)
            }

            else -> Unit
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = mainSnackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    if (active) active = false
                    focusManager.clearFocus()
                }
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())

            DockedSearchBar(
                modifier = Modifier.fillMaxWidth(),
                query = viewModel.searchQuery,
                onQueryChange = { viewModel.onQueryChange(it) },
                onSearch = { query ->
                    viewModel.searchMovies()
                    active = false
                    keyboardController?.hide()
                    focusManager.clearFocus()
                },
                active = active,
                onActiveChange = {
                    active = it
                    if (!active) focusManager.clearFocus()
                },
                placeholder = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = {
                    if (active) {
                        IconButton(onClick = {
                            active = false
                            focusManager.clearFocus()
                        }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.close)
                            )
                        }
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                },
                trailingIcon = {
                    Row {
                        if (viewModel.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChange("") }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(R.string.clear)
                                )
                            }
                        }
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                Icons.Default.List,
                                contentDescription = stringResource(R.string.filter)
                            )
                        }
                    }
                },
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                val scrollState = rememberScrollState()
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    if (searchHistory.isNotEmpty()) {
                        SearchHistoryList(
                            history = searchHistory,
                            onItemClick = { query ->
                                viewModel.onHistoryClick(query)
                                active = false
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.no_search_history),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!viewModel.hasSearched) {
                EmptyStateMessage()
            } else {
                val loadState = movies.loadState
                when (loadState.refresh) {
                    is LoadState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is LoadState.Error -> {
                        val error = (loadState.refresh as LoadState.Error).error
                        ErrorItem(
                            message = error.localizedMessage
                                ?: stringResource(R.string.error_unknown),
                            onRetry = { movies.retry() }
                        )
                    }

                    is LoadState.NotLoading -> {
                        if (movies.itemCount == 0) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(stringResource(R.string.no_results))
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(movies.itemCount) { index ->
                                    val movie = movies[index]
                                    if (movie != null) {
                                        val isLiked = likedMovieIds.contains(movie.id)
                                        MovieCard(
                                            movie = movie,
                                            isFavorite = isLiked,
                                            onMovieClick = {
                                                focusManager.clearFocus()
                                                viewModel.onMovieClicked(movie.id)
                                            },
                                            onToggleFavorite = {
                                                selectedMovieForFavorites = movie
                                                viewModel.fetchListsForMovie(movie.id)
                                            }
                                        )
                                    }
                                }
                                if (loadState.append is LoadState.Loading) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(modifier = Modifier.size(30.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            currentFilter = viewModel.filterState,
            onApply = { newFilter -> viewModel.updateFilter(newFilter) },
            onDismiss = { showFilterSheet = false }
        )
    }

    if (selectedMovieForFavorites != null) {
        AddToFavoritesSheet(
            lists = favoriteLists,
            snackBarHostState = sheetSnackbarHostState,
            onDismiss = { selectedMovieForFavorites = null },
            onToggleList = { listId, isChecked ->
                viewModel.toggleMovieInList(
                    listId = listId,
                    //there is a null check upside
                    movie = selectedMovieForFavorites!!,
                    isChecked = isChecked
                )
            },
            onCreateList = { listName ->
                viewModel.createNewList(listName)
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToFavoritesSheet(
    lists: List<FavoriteListUiModel>,
    snackBarHostState: SnackbarHostState,
    onDismiss: () -> Unit,
    onToggleList: (Long, Boolean) -> Unit,
    onCreateList: (String) -> Unit
) {
    var newListName by remember { mutableStateOf("") }
    var showCreateInput by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        windowInsets = WindowInsets.ime
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 48.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_to_list_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(
                        count = lists.size,
                        key = { index -> lists[index].id }
                    ) { index ->
                        val list = lists[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleList(list.id, !list.isMovieInList) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = list.isMovieInList,
                                onCheckedChange = { isChecked ->
                                    onToggleList(list.id, isChecked)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = list.name, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                if (showCreateInput) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newListName,
                            onValueChange = { newListName = it },
                            placeholder = { Text(stringResource(R.string.list_name_hint)) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onCreateList(newListName)
                                newListName = ""
                                showCreateInput = false
                            },
                            enabled = newListName.isNotBlank()
                        ) {
                            Text(stringResource(R.string.create))
                        }
                    }
                } else {
                    TextButton(
                        onClick = { showCreateInput = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.create_new_list))
                    }
                }
            }

            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun SearchHistoryList(history: List<SearchHistoryEntity>, onItemClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.recent_searches),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.primary
        )

        history.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item.query) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.query,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun EmptyStateMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorItem(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.retry))
        }
    }
}