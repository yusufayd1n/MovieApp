package com.example.movieapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.movieapp.R
import com.example.movieapp.common.GenreConstants
import com.example.movieapp.ui.feature.search.FilterState
import com.example.movieapp.ui.feature.search.SearchViewModel
import com.example.movieapp.ui.feature.search.SortOption

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    currentFilter: FilterState,
    onApply: (FilterState) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedYear by remember { mutableStateOf(currentFilter.selectedYear) }
    var selectedMinVote by remember { mutableIntStateOf(currentFilter.minVote) }
    var selectedGenreId by remember { mutableStateOf(currentFilter.selectedGenreId) }
    var selectedSortOption by remember { mutableStateOf(currentFilter.sortOption) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.filter_title),
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Text(
                text = stringResource(R.string.filter_year),
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = selectedYear,
                onValueChange = { if (it.length <= 4) selectedYear = it },
                placeholder = { Text(stringResource(R.string.filter_year_hint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.sort_title),
                style = MaterialTheme.typography.titleMedium
            )
            Column(modifier = Modifier.padding(top = 8.dp)) {
                SortOption.entries.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .selectable(
                                selected = (selectedSortOption == option),
                                onClick = { selectedSortOption = option },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedSortOption == option),
                            onClick = null
                        )
                        Text(
                            text = stringResource(id = option.titleResId),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.filter_score),
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(4, 5, 6, 7, 8).forEach { vote ->
                    FilterChip(
                        selected = (selectedMinVote == vote),
                        onClick = {
                            selectedMinVote = if (selectedMinVote == vote) 0 else vote
                        },
                        label = { Text("$vote+") },
                        leadingIcon = if (selectedMinVote == vote) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.filter_genre),
                style = MaterialTheme.typography.titleMedium
            )
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GenreConstants.genreMap.forEach { (id, nameResId) ->
                    FilterChip(
                        selected = (selectedGenreId == id),
                        onClick = {
                            selectedGenreId = if (selectedGenreId == id) null else id
                        },
                        label = {
                            Text(stringResource(id = nameResId))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onApply(
                        FilterState(
                            selectedYear = selectedYear,
                            minVote = selectedMinVote,
                            selectedGenreId = selectedGenreId,
                            sortOption = selectedSortOption
                        )
                    )
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.apply))
            }

            TextButton(
                onClick = {
                    selectedYear = ""
                    selectedMinVote = 0
                    selectedGenreId = null
                    selectedSortOption = SortOption.DEFAULT
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.clear_filters))
            }
        }
    }
}