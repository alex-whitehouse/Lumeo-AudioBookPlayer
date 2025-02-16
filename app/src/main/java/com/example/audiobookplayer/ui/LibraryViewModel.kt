package com.example.audiobookplayer.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.audiobookplayer.common.enums.BookStatus
import com.example.audiobookplayer.data.AudiobookEntity
import com.example.audiobookplayer.domain.AudiobookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: AudiobookRepository,
    private val preferences: PreferencesManager
) : ViewModel() {

    val books = Pager(PagingConfig(pageSize = 20)) {
        when(preferences.getSortOption() ?: SortOption.RECENT) {
            SortOption.RECENT -> repository.getPagedBooksSortedByRecent()
            SortOption.TITLE -> repository.getPagedBooksSortedByTitle()
            SortOption.PROGRESS -> repository.getPagedBooksSortedByProgress()
        }
    }.flow.cachedIn(viewModelScope)

    var sortOption by mutableStateOf(SortOption.RECENT)
        private set

    var filterStatus by mutableStateOf(FilterStatus.ALL)
        private set

    init {
        loadPreferences()
    }

    fun setSortOption(option: SortOption) {
        sortOption = option
        preferences.saveSortOption(option)
    }

    fun setFilterStatus(status: FilterStatus) {
        filterStatus = status
        preferences.saveFilterStatus(status)
    }

    fun refreshLibrary() {
        // Trigger refresh through repository
        viewModelScope.launch {
            repository.refreshLibrary()
        }
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            sortOption = preferences.getSortOption() ?: SortOption.RECENT
            filterStatus = preferences.getFilterStatus() ?: FilterStatus.ALL
        }
    }
}

enum class SortOption {
    RECENT, TITLE, PROGRESS
}

enum class FilterStatus(val displayName: String) {
    ALL("All"), NEW("New"), STARTED("In Progress"), FINISHED("Completed");

    fun toBookStatus(): BookStatus? = when(this) {
        ALL -> null
        NEW -> BookStatus.NEW
        STARTED -> BookStatus.STARTED
        FINISHED -> BookStatus.FINISHED
    }
}
