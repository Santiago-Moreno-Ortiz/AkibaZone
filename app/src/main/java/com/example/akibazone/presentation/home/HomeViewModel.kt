package com.example.akibazone.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.akibazone.domain.model.Anime
import com.example.akibazone.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.akibazone.domain.usecase.GetHomeDataUseCase
import com.example.akibazone.domain.usecase.HomeData
import com.example.akibazone.domain.usecase.SearchAnimeUseCase
import com.example.akibazone.presentation.state.UiState

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
                _selectedGenre.value = "Todos"
                _catalogList.value = if (data.catalog.isNotEmpty()) data.catalog else data.popular
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _uiState.value = UiState.Error(e.message ?: "Error al cargar datos")
            }
        }
    }

    private var catalogJob: kotlinx.coroutines.Job? = null

    fun selectGenre(genre: String) {
        if (_selectedGenre.value == genre && _catalogList.value.isNotEmpty()) return
        _selectedGenre.value = genre
        catalogJob?.cancel()
        catalogJob = viewModelScope.launch {
            _isCatalogLoading.value = true
            try {
                val genreFilter = if (genre == "Todos") null else genre
                val results = searchAnimeUseCase(query = "", genre = genreFilter, sort = "POPULARITY_DESC")
                _catalogList.value = results
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _uiState.value = UiState.Error("No se pudo filtrar el catálogo. Comprueba tu conexión y reintenta.")
            } finally {
                _isCatalogLoading.value = false
            }
        }
    }
}
