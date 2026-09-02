package com.example.projectjuansantiagoaby.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectjuansantiagoaby.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PlayerUiState {
    object Loading : PlayerUiState
    data class Success(val videoUrl: String) : PlayerUiState
    data class Error(val message: String) : PlayerUiState
}

class PlayerViewModel(private val repository: AnimeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadVideo(episodeId: String) {
        viewModelScope.launch {
            _uiState.value = PlayerUiState.Loading
            try {
                // episodeId es el link completo del episodio o el id para la API
                val links = repository.getVideoLinks(episodeId)
                if (links.isNotEmpty()) {
                    _uiState.value = PlayerUiState.Success(links.first())
                } else {
                    _uiState.value = PlayerUiState.Error("No se encontraron servidores")
                }
            } catch (e: Exception) {
                _uiState.value = PlayerUiState.Error(e.message ?: "Error")
            }
        }
    }
}
