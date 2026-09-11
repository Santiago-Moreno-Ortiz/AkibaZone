package com.example.projectjuansantiagoaby.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectjuansantiagoaby.domain.model.Anime
import com.example.projectjuansantiagoaby.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.projectjuansantiagoaby.domain.usecase.GetHomeDataUseCase
import com.example.projectjuansantiagoaby.domain.usecase.HomeData
import com.example.projectjuansantiagoaby.domain.usecase.SearchAnimeUseCase
import com.example.projectjuansantiagoaby.presentation.state.UiState

class HomeViewModel(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val searchAnimeUseCase: SearchAnimeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeData>> = _uiState.asStateFlow()

    private val _selectedGenre = MutableStateFlow("Todos")
    val selectedGenre: StateFlow<String> = _selectedGenre.asStateFlow()

    private val _catalogList = MutableStateFlow<List<Anime>>(emptyList())
    val catalogList: StateFlow<List<Anime>> = _catalogList.asStateFlow()

    private val _isCatalogLoading = MutableStateFlow(false)
    val isCatalogLoading: StateFlow<Boolean> = _isCatalogLoading.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val data = getHomeDataUseCase()
                _uiState.value = UiState.Success(data)
                _catalogList.value = if (data.catalog.isNotEmpty()) data.catalog else data.popular
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al cargar datos")
            }
        }
    }

    fun selectGenre(genre: String) {
        if (_selectedGenre.value == genre && _catalogList.value.isNotEmpty()) return
        _selectedGenre.value = genre
        viewModelScope.launch {
            _isCatalogLoading.value = true
            try {
                val genreFilter = if (genre == "Todos") null else genre
                val results = searchAnimeUseCase(query = "", genre = genreFilter, sort = "POPULARITY_DESC")
                if (results.isNotEmpty()) {
                    _catalogList.value = results
                } else if (genre == "Todos") {
                    val currentData = (_uiState.value as? UiState.Success)?.data
                    if (currentData != null) {
                        _catalogList.value = if (currentData.catalog.isNotEmpty()) currentData.catalog else currentData.popular
                    }
                }
            } catch (e: Exception) {
                // Keep existing catalog list if search fails
            } finally {
                _isCatalogLoading.value = false
            }
        }
    }
}
