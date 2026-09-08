package com.example.projectjuansantiagoaby.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectjuansantiagoaby.data.repository.AnimeRepository
import com.example.projectjuansantiagoaby.domain.model.Anime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.projectjuansantiagoaby.domain.usecase.SearchAnimeUseCase
import com.example.projectjuansantiagoaby.presentation.state.UiState

class ExploreViewModel(private val searchAnimeUseCase: SearchAnimeUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Anime>>>(UiState.Success(emptyList()))
    val uiState = _uiState.asStateFlow()

    fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val results = searchAnimeUseCase(query)
                _uiState.value = UiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al buscar")
            }
        }
    }
}
