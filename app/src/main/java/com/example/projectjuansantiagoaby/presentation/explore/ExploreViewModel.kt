package com.example.projectjuansantiagoaby.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectjuansantiagoaby.data.repository.AnimeRepository
import com.example.projectjuansantiagoaby.domain.model.Anime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ExploreUiState {
    object Idle : ExploreUiState
    object Loading : ExploreUiState
    data class Success(val results: List<Anime>) : ExploreUiState
    data class Error(val message: String) : ExploreUiState
}

class ExploreViewModel(private val repository: AnimeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _uiState.value = ExploreUiState.Loading
            try {
                val results = repository.searchAnime(query)
                _uiState.value = ExploreUiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = ExploreUiState.Error(e.message ?: "Error")
            }
        }
    }
}
