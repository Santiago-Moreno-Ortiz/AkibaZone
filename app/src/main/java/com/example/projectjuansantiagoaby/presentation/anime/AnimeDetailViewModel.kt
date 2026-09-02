package com.example.projectjuansantiagoaby.presentation.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectjuansantiagoaby.domain.model.AnimeDetail
import com.example.projectjuansantiagoaby.data.repository.AnimeRepository
import com.example.projectjuansantiagoaby.domain.model.Anime
import com.example.projectjuansantiagoaby.domain.model.Episode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val detail: AnimeDetail) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class AnimeDetailViewModel(private val repository: AnimeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadAnimeDetail(animeId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val detail = repository.getAnimeDetail(animeId)
                if (detail != null) {
                    _uiState.value = DetailUiState.Success(detail)
                } else {
                    _uiState.value = DetailUiState.Error("No se encontró información")
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
