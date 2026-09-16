package com.example.akibazone.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.akibazone.data.preferences.SettingsRepository
import com.example.akibazone.data.preferences.ThemePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val themePreference: StateFlow<ThemePreference> = repository.themePreference
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemePreference.SYSTEM)

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun setTheme(theme: ThemePreference) {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                repository.setTheme(theme)
            } catch (exception: Exception) {
                if (exception is kotlinx.coroutines.CancellationException) throw exception
                _errorMessage.value = "No se pudo guardar el tema seleccionado."
            }
        }
    }
}
