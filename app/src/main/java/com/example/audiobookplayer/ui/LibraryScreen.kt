package com.example.audiobookplayer.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.audiobookplayer.R
import com.example.audiobookplayer.common.enums.BookStatus
import com.example.audiobookplayer.common.enums.FilterStatus
import com.example.audiobookplayer.common.enums.SortOption
import com.example.audiobookplayer.common.enums.FilterStatus
import com.example.audiobookplayer.common.enums.SortOption
import com.example.audiobookplayer.domain.model.Book
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onBookClick: (String) -> Unit
) {
    var viewMode by remember { mutableStateOf(ViewMode.GRID) }
    val books = viewModel.books.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.library_title)) },
                actions = {
                    // Sort Menu
                    val sortOptions = mapOf(
                        SortOption.RECENT to stringResource(R.string.sort_recent),
                        SortOption.TITLE to stringResource(R.string.sort_title),
                        SortOption.PROGRESS to stringResource(R.string.sort_progress)
                    )
                    var showSortMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        sortOptions.forEach { (option, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    viewModel.setSortOption(option)
                                    showSortMenu = false
                                },
                                leadingIcon = {
                                    if (viewModel.sortOption == option) {
                                        Icon(Icons.Default.Check, contentDescription = "Selected")
                                    } else {
                                        Spacer(modifier = Modifier.size(24.dp))
                                    }
                                }
                            )
                        }
                    }
                    // Filter Menu
                    val filterOptions = mapOf(
                        FilterStatus.ALL to stringResource(R.string.filter_all),
                        FilterStatus.NEW to stringResource(R.string.filter_new),
                        FilterStatus.STARTED to stringResource(R.string.filter_started),
                        FilterStatus.FINISHED to stringResource(R.string.filter_finished)
                    )
                    var showFilterMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        filterOptions.forEach { (option, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    viewModel.setFilterStatus(option)
                                    showFilterMenu = false
                                },
                                leadingIcon = {
                                    if (viewModel.filterStatus == option) {
                                        Icon(Icons.Default.Check, contentDescription = "Selected")
                                    } else {
                                        Spacer(modifier = Modifier.size(24.dp))
                                    }
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // View mode toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = viewMode == ViewMode.LIST,
                    onClick = { viewMode = ViewMode.LIST },
                    label = { Text(stringResource(R.string.view_list)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = viewMode == ViewMode.GRID,
                    onClick = { viewMode = ViewMode.GRID },
                    label = { Text(stringResource(R.string.view_grid)) }
                )
            }

            when (viewMode) {
                ViewMode.GRID -> BookGrid(books, onBookClick)
                ViewMode.LIST -> BookList(books, onBookClick)
            }

            // Handle loading states
            when (books.loadState.refresh) {
                is LoadState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is LoadState.Error -> {
                    val error = (books.loadState.refresh as LoadState.Error).error
                    ErrorView(error) {
                        books.retry()
                    }
                }
                else -> {
                    if (books.itemCount == 0) {
                        EmptyLibraryScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun BookGrid(
    books: LazyPagingItems<Book>,
    onBookClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(128.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books.itemCount) { index ->
            books[index]?.let { book ->
                BookCard(book, onBookClick)
            }
        }

        books.apply {
            when {
                loadState.append is LoadState.Loading -> item { LoadingItem() }
                loadState.append is LoadState.Error -> item {
                    ErrorItem { retry() }
                }
            }
        }
    }
}

@Composable
private fun BookCard(
    book: Book,
    onBookClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(3f / 4f)
            .clickable { onBookClick(book.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = stringResource(R.string.book_cover, book.title),
                    placeholder = painterResource(R.drawable.ic_default_cover),
                    error = painterResource(R.drawable.ic_default_cover),
                    modifier = Modifier.fillMaxWidth()
                )
                
                LinearProgressIndicator(
                    progress = book.progress ?: 0f,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )
            }
            
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = book.title ?: stringResource(R.string.unknown_title),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = book.author ?: stringResource(R.string.unknown_author),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                StatusDot(book.status)
            }
        }
    }
}
