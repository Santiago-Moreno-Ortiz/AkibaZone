package com.example.akibazone.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

enum class ThemePreference(val storedValue: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromStoredValue(value: String?): ThemePreference =
            entries.firstOrNull { it.storedValue == value } ?: SYSTEM
    }
}

private val Context.settingsDataStore by preferencesDataStore(name = "akibazone_settings")

private object SettingsKeys {
    val theme = stringPreferencesKey("theme")
}

class SettingsRepository(private val context: Context) {

    val themePreference: Flow<ThemePreference> = context.settingsDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences: Preferences ->
            ThemePreference.fromStoredValue(preferences[SettingsKeys.theme])
        }

    suspend fun setTheme(theme: ThemePreference) {
        context.settingsDataStore.edit { preferences ->
            preferences[SettingsKeys.theme] = theme.storedValue
        }
    }
}
