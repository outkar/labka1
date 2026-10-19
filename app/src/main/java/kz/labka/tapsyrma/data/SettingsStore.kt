package kz.labka.tapsyrma.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kz.labka.tapsyrma.domain.SortOrder
import kz.labka.tapsyrma.domain.StatusFilter
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/** Тізімнің сұрыптау тәртібі мен сүзгісін DataStore-да сақтайды. */
class SettingsStore(private val context: Context) {

    private val preferences: Flow<Preferences> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }

    val sortOrder: Flow<SortOrder> = preferences.map { prefs ->
        SortOrder.entries.firstOrNull { it.name == prefs[SORT_KEY] } ?: SortOrder.DUE_DATE
    }

    val statusFilter: Flow<StatusFilter> = preferences.map { prefs ->
        StatusFilter.entries.firstOrNull { it.name == prefs[FILTER_KEY] } ?: StatusFilter.ALL
    }

    suspend fun setSortOrder(order: SortOrder) {
        context.dataStore.edit { it[SORT_KEY] = order.name }
    }

    suspend fun setStatusFilter(filter: StatusFilter) {
        context.dataStore.edit { it[FILTER_KEY] = filter.name }
    }

    private companion object {
        val SORT_KEY = stringPreferencesKey("sort_order")
        val FILTER_KEY = stringPreferencesKey("status_filter")
    }
}
