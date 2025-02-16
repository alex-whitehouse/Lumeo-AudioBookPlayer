package com.example.audiobookplayer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.PagingData
import com.example.audiobookplayer.common.enums.BookStatus
import com.example.audiobookplayer.common.enums.FilterStatus
import com.example.audiobookplayer.common.enums.SortOption
import com.example.audiobookplayer.data.AudiobookEntity
import com.example.audiobookplayer.data.AudiobookRepositoryPreferencesManager
import com.example.audiobookplayer.domain.AudiobookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: AudiobookRepository,
    private val preferencesManager: AudiobookRepositoryPreferencesManager
) : ViewModel() {

    private val sortFlow = MutableStateFlow(SortOption.RECENT)
    private val filterFlow = MutableStateFlow(FilterStatus.ALL)

    val books: Flow<PagingData<AudiobookEntity>> = combine(sortFlow, filterFlow) { sort, filter ->
        Pair(sort, filter)
    }.mapLatest { (sort, filter) ->
        when (sort) {
            SortOption.RECENT -> when (filter) {
                FilterStatus.ALL -> repository.getPagedBooksSortedByRecent()
                FilterStatus.NEW -> repository.getPagedNewBooksSortedByRecent()
                FilterStatus.STARTED -> repository.getPagedStartedBooksSortedByRecent()
                FilterStatus.FINISHED -> repository.getPagedFinishedBooksSortedByRecent()
            }
            SortOption.TITLE -> when (filter) {
                FilterStatus.ALL -> repository.getPagedBooksSortedByTitle()
                FilterStatus.NEW -> repository.getPagedNewBooksSortedByTitle()
                FilterStatus.STARTED -> repository.getPagedStartedBooksSortedByTitle()
                FilterStatus.FINISHED -> repository.getPagedFinishedBooksSortedByTitle()
            }
            SortOption.PROGRESS -> when (filter) {
                FilterStatus.ALL -> repository.getPagedBooksSortedByProgress()
                FilterStatus.NEW -> repository.getPagedNewBooksSortedByProgress()
                FilterStatus.STARTED -> repository.getPagedStartedBooksSortedByProgress()
                FilterStatus.FINISHED -> repository.getPagedFinishedBooksSortedByProgress()
            }
        }
    }.flatMapLatest { it }.cachedIn(viewModelScope)

    val sortOption: StateFlow<SortOption> = sortFlow
    val filterStatus: StateFlow<FilterStatus> = filterFlow

    init {
        viewModelScope.launch {
            sortFlow.value = preferencesManager.getSortOption() ?: SortOption.RECENT
            filterFlow.value = preferencesManager.getFilterStatus() ?: FilterStatus.ALL
        }
    }

    fun setSortOption(option: SortOption) {
        if (sortFlow.value != option) {
            sortFlow.value = option
            viewModelScope.launch {
                preferencesManager.saveSortOption(option)
            }
        }
    }

    fun setFilterStatus(status: FilterStatus) {
        if (filterFlow.value != status) {
            filterFlow.value = status
            viewModelScope.launch {
                preferencesManager.saveFilterPreference(status)
            }
        }
    }

    fun refreshLibrary() {
        viewModelScope.launch {
            repository.refreshLibrary()
        }
    }
}
