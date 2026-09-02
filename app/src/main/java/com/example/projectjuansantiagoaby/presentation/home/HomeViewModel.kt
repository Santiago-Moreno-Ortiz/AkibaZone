package com.example.projectjuansantiagoaby.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectjuansantiagoaby.domain.model.Anime
import com.example.projectjuansantiagoaby.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val popularAnime: List<Anime>,
        val latestReleases: List<Anime>,
        val trending: List<Anime>
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(private val repository: AnimeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val popular = repository.getPopularAnime()
                val latest = repository.getLatestReleases()
                
                _uiState.value = HomeUiState.Success(
                    popularAnime = popular,
                    latestReleases = latest,
                    trending = popular.take(5)
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
