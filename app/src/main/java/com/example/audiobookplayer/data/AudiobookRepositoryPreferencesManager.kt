package com.example.audiobookplayer.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_preferences")

class AudiobookRepositoryPreferencesManager(private val context: Context) {

    companion object {
        private val SORT_OPTION_KEY = stringPreferencesKey("sort_option")
        private val FILTER_STATUS_KEY = stringPreferencesKey("filter_status")
        private val GRID_VIEW_KEY = stringPreferencesKey("grid_view")
    }

    val sortOptionFlow: Flow<com.example.audiobookplayer.ui.SortOption?> =
        context.dataStore.data.map { preferences ->
            preferences[SORT_OPTION_KEY]?.let { 
                com.example.audiobookplayer.ui.SortOption.valueOf(it)
            } ?: com.example.audiobookplayer.ui.SortOption.RECENT
        }

    val filterStatusFlow: Flow<com.example.audiobookplayer.ui.FilterStatus?> =
        context.dataStore.data.map { preferences ->
            preferences[FILTER_STATUS_KEY]?.let { 
                com.example.audiobookplayer.ui.FilterStatus.valueOf(it)
            } ?: com.example.audiobookplayer.ui.FilterStatus.ALL
        }

    suspend fun saveSortOption(sortOption: com.example.audiobookplayer.ui.SortOption) {
        context.dataStore.edit { preferences ->
            preferences[SORT_OPTION_KEY] = sortOption.name
        }
    }

    suspend fun saveFilterStatus(filterStatus: com.example.audiobookplayer.ui.FilterStatus) {
        context.dataStore.edit { preferences ->
            preferences[FILTER_STATUS_KEY] = filterStatus.name
        }
    }

    suspend fun getSortOption(): com.example.audiobookplayer.ui.SortOption? {
        return context.dataStore.data.map { preferences ->
            preferences[SORT_OPTION_KEY]?.let { 
                com.example.audiobookplayer.ui.SortOption.valueOf(it)
            }
        }.firstOrNull()
    }

    suspend fun getFilterStatus(): com.example.audiobookplayer.ui.FilterStatus? {
        return context.dataStore.data.map { preferences ->
            preferences[FILTER_STATUS_KEY]?.let { 
                com.example.audiobookplayer.ui.FilterStatus.valueOf(it)
            }
        }.firstOrNull()
    }

    suspend fun saveGridView(isGridView: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[GRID_VIEW_KEY] = if (isGridView) "true" else "false"
        }
    }

    val gridViewFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[GRID_VIEW_KEY]?.toBoolean() ?: true
    }
}
