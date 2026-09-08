package com.example.projectjuansantiagoaby.presentation.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectjuansantiagoaby.domain.model.Anime
import com.example.projectjuansantiagoaby.domain.model.AnimeDetail
import com.example.projectjuansantiagoaby.domain.usecase.GetAnimeDetailUseCase
import com.example.projectjuansantiagoaby.domain.usecase.ToggleFavoriteUseCase
import com.example.projectjuansantiagoaby.presentation.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnimeDetailViewModel(
    private val getAnimeDetailUseCase: GetAnimeDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AnimeDetail>>(UiState.Loading)
    val uiState: StateFlow<UiState<AnimeDetail>> = _uiState.asStateFlow()

    fun loadAnimeDetail(animeId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val detail = getAnimeDetailUseCase(animeId)
                if (detail != null) {
                    _uiState.value = UiState.Success(detail)
                } else {
                    _uiState.value = UiState.Error("No se encontró información")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun toggleFavorite(anime: Anime) {
        viewModelScope.launch {
            toggleFavoriteUseCase(anime)
            // Recargar para actualizar el icono
            loadAnimeDetail(anime.id)
        }
    }
}
