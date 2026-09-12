package com.example.akibazone.presentation.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.akibazone.domain.model.Anime
import com.example.akibazone.domain.model.AnimeDetail
import com.example.akibazone.domain.usecase.GetAnimeDetailUseCase
import com.example.akibazone.domain.usecase.ToggleFavoriteUseCase
import com.example.akibazone.presentation.state.UiState
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
                if (e is kotlinx.coroutines.CancellationException) throw e
                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private var favoriteJob: kotlinx.coroutines.Job? = null

    fun toggleFavorite(anime: Anime) {
        if (favoriteJob?.isActive == true) return
        favoriteJob = viewModelScope.launch {
            try {
                toggleFavoriteUseCase(anime)
                val detail = (_uiState.value as? UiState.Success)?.data ?: return@launch
                _uiState.value = UiState.Success(detail.copy(anime = detail.anime.copy(isFavorite = !detail.anime.isFavorite)))
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _uiState.value = UiState.Error("No se pudo guardar el favorito")
            }
        }
    }
}
