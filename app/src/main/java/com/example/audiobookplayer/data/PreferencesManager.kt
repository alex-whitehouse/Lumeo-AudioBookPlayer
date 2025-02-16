package com.example.audiobookplayer.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_preferences")

class PreferencesManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val SORT_TYPE_KEY = stringPreferencesKey("sort_type")
        val FILTER_TYPE_KEY = stringPreferencesKey("filter_type")
        val GRID_VIEW_KEY = booleanPreferencesKey("grid_view")
    }

    val sortTypeFlow: Flow<String?> = dataStore.data
        .map { preferences -> preferences[SORT_TYPE_KEY] }

    val filterTypeFlow: Flow<String?> = dataStore.data
        .map { preferences -> preferences[FILTER_TYPE_KEY] }

    val gridViewFlow: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[GRID_VIEW_KEY] ?: true }

    suspend fun saveSortType(sortType: String) {
        dataStore.edit { preferences ->
            preferences[SORT_TYPE_KEY] = sortType
        }
    }

    suspend fun saveFilterType(filterType: String) {
        dataStore.edit { preferences ->
            preferences[FILTER_TYPE_KEY] = filterType
        }
    }

    suspend fun saveGridView(isGridView: Boolean) {
        dataStore.edit { preferences ->
            preferences[GRID_VIEW_KEY] = isGridView
        }
    }
}
