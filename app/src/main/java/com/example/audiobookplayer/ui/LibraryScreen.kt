package com.example.audiobookplayer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.audiobookplayer.R
import com.example.audiobookplayer.common.enums.BookStatus
import com.example.audiobookplayer.domain.model.Book
import com.example.audiobookplayer.ui.theme.Blue500
import com.example.audiobookplayer.ui.theme.Green500
import com.example.audiobookplayer.ui.theme.Yellow500

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onBookClick: (String) -> Unit // bookId
) {
    var viewMode by remember { mutableStateOf(ViewMode.Grid) }
    val books = viewModel.books.collectAsLazyPagingItems()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.library_title)) },
                actions = {
val sortOptions = listOf(
    SortOption.RECENT to "Recent",
    SortOption.TITLE to "Title",
    SortOption.PROGRESS to "Progress"
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
val filterOptions = listOf(
    FilterStatus.ALL to "All",
    FilterStatus.NEW to "New",
    FilterStatus.STARTED to "In Progress",
    FilterStatus.FINISHED to "Completed"
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
                    selected = viewMode == ViewMode.List,
                    onClick = { viewMode = ViewMode.List },
                    label = { Text("List") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = viewMode == ViewMode.Grid,
                    onClick = { viewMode = ViewMode.Grid },
                    label = { Text("Grid") }
                )
            }

            when (viewMode) {
                ViewMode.Grid -> BookGrid(books, onBookClick)
                ViewMode.List -> BookList(books, onBookClick)
            }

            // Handle loading states
            if (books.itemCount == 0 && books.loadState.refresh is LoadState.NotLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_empty_state),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(128.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.empty_library_message),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
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
                        ErrorMessage(error.message ?: "Unknown error")
                    }
                    else -> Unit
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
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books) { book ->
            if (book != null) {
                BookCard(book, onBookClick)
            }
        }
    }
}

@Composable
private fun BookList(
    books: LazyPagingItems<Book>,
    onBookClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(books) { book ->
            if (book != null) {
                ListItem(
                    headlineContent = { Text(book.title) },
                    supportingContent = { Text(book.author) },
                    leadingContent = {
                        AsyncImage(
                            model = book.coverUrl,
                            contentDescription = null,
                            placeholder = painterResource(R.drawable.ic_default_cover),
                            error = painterResource(R.drawable.ic_default_cover),
                            modifier = Modifier.size(56.dp)
                        )
                    },
                    trailingContent = {
                        Column(horizontalAlignment = Alignment.End) {
                            LinearProgressIndicator(
                                progress = book.progress,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            StatusDot(book.status)
                        }
                    },
                    modifier = Modifier.clickable { onBookClick(book.id) }
                )
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
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.ic_default_cover),
                    error = painterResource(R.drawable.ic_default_cover),
                    modifier = Modifier.fillMaxWidth()
                )
                
                LinearProgressIndicator(
                    progress = book.progress,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )
            }
            
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = book.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = book.author,
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

@Composable
private fun StatusDot(status: BookStatus) {
    val color = when (status) {
        BookStatus.NEW -> Blue500
        BookStatus.STARTED -> Yellow500
        BookStatus.FINISHED -> Green500
    }
    
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(color = color, shape = CircleShape)
    )
}

@Composable
private fun ErrorMessage(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_empty_state),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

enum class ViewMode {
    Grid, List
}
