package com.example.akibazone.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.akibazone.data.model.Anime as StoredAnime
import com.example.akibazone.data.repository.AnimeRepository
import com.example.akibazone.data.repository.toDomain
import com.example.akibazone.domain.model.Anime
import com.example.akibazone.presentation.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: AnimeRepository) : ViewModel() {

    val historyState: StateFlow<UiState<List<Anime>>> = repository.history.asFlow()
        .map(::toUiState)
        .catch { emit(UiState.Error("No se pudo cargar el historial.")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError.asStateFlow()

    private var clearJob: Job? = null

    fun clearHistory() {
        if (clearJob?.isActive == true) return

        clearJob = viewModelScope.launch {
            _actionError.value = null
            try {
                repository.clearHistory()
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _actionError.value = "No se pudo borrar el historial."
            }
        }
    }

    private fun toUiState(list: List<StoredAnime>): UiState<List<Anime>> =
        UiState.Success(list.map { it.toDomain() })
}
