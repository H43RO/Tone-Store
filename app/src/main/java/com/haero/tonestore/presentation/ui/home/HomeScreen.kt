package com.haero.tonestore.presentation.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.ToneSetting
import com.haero.tonestore.presentation.ui.home.components.ToneSettingCard
import com.haero.tonestore.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * 홈 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // 네비게이션 처리
    LaunchedEffect(state.navigateToCreate) {
        if (state.navigateToCreate) {
            onNavigateToCreate()
            viewModel.handleIntent(HomeIntent.NavigationHandled)
        }
    }

    LaunchedEffect(state.navigateToDetail) {
        state.navigateToDetail?.let { id ->
            onNavigateToDetail(id)
            viewModel.handleIntent(HomeIntent.NavigationHandled)
        }
    }

    Scaffold(
        topBar = {
            if (state.isSearchActive.not()) {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(
                            onClick = { viewModel.handleIntent(HomeIntent.SetSearchActive(true)) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleIntent(HomeIntent.NavigateToCreate) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_tone_setting)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 검색바
            if (state.isSearchActive) {
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.handleIntent(HomeIntent.UpdateSearchQuery(it)) },
                    onSearch = { },
                    active = false,
                    onActiveChange = { },
                    placeholder = { Text(stringResource(R.string.search_hint)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { viewModel.handleIntent(HomeIntent.SetSearchActive(false)) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.close_search)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) { }
            }

            // 컨텐츠
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    state.filteredToneSettings.isEmpty() && state.searchQuery.isNotBlank() -> {
                        Text(
                            text = stringResource(R.string.no_search_results),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp)
                        )
                    }
                    state.toneSettings.isEmpty() -> {
                        Text(
                            text = stringResource(R.string.empty_tone_settings),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp)
                        )
                    }
                    else -> {
                        ToneSettingList(
                            toneSettings = state.filteredToneSettings,
                            onItemClick = { id ->
                                viewModel.handleIntent(HomeIntent.SelectToneSetting(id))
                            },
                            onDelete = { id ->
                                viewModel.handleIntent(HomeIntent.DeleteToneSetting(id))
                            },
                            onFavoriteClick = { id ->
                                viewModel.handleIntent(HomeIntent.ToggleFavorite(id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToneSettingList(
    toneSettings: List<ToneSetting>,
    onItemClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = toneSettings,
            key = { it.id }
        ) { toneSetting ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { dismissValue ->
                    if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                        showDeleteDialog = toneSetting.id
                    }
                    false
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    val color by animateColorAsState(
                        when (dismissState.targetValue) {
                            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                            else -> Color.Transparent
                        },
                        label = "swipe_color"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                },
                enableDismissFromStartToEnd = false,
                enableDismissFromEndToStart = true
            ) {
                ToneSettingCard(
                    toneSetting = toneSetting,
                    onClick = { onItemClick(toneSetting.id) },
                    onFavoriteClick = { onFavoriteClick(toneSetting.id) }
                )
            }
        }
    }

    // 삭제 확인 다이얼로그
    showDeleteDialog?.let { id ->
        val toneSetting = toneSettings.find { it.id == id }
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.delete_confirm_message,
                        toneSetting?.songName ?: ""
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(id)
                        showDeleteDialog = null
                    }
                ) {
                    Text(
                        stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
