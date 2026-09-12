package com.example.akibazone.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.akibazone.domain.model.Anime
import com.example.akibazone.domain.usecase.SearchAnimeUseCase
import com.example.akibazone.presentation.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExploreFilterState(
    val query: String = "",
    val selectedGenre: String? = null,
    val selectedFormat: String? = null,
    val selectedSort: String = "POPULARITY_DESC"
)

class ExploreViewModel(private val searchAnimeUseCase: SearchAnimeUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Anime>>>(UiState.Success(emptyList()))
    val uiState = _uiState.asStateFlow()

    private val _filterState = MutableStateFlow(ExploreFilterState())
    val filterState = _filterState.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Cargar animes iniciales populares/tendencias al entrar
        applyFilters()
    }

    fun onQueryChange(newQuery: String) {
        _filterState.value = _filterState.value.copy(query = newQuery)
        debounceSearch()
    }

    fun onGenreSelected(genre: String?) {
        val current = _filterState.value.selectedGenre
        val newGenre = if (current == genre) null else genre
        _filterState.value = _filterState.value.copy(selectedGenre = newGenre)
        applyFilters()
    }

    fun onFormatSelected(format: String?) {
        val current = _filterState.value.selectedFormat
        val newFormat = if (current == format) null else format
        _filterState.value = _filterState.value.copy(selectedFormat = newFormat)
        applyFilters()
    }

    fun onSortSelected(sort: String) {
        _filterState.value = _filterState.value.copy(selectedSort = sort)
        applyFilters()
    }

    fun clearFilters() {
        _filterState.value = ExploreFilterState()
        applyFilters()
    }

    private fun debounceSearch() = search(debounce = true)

    fun applyFilters() = search(debounce = false)

    private fun search(debounce: Boolean) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (debounce) delay(400)
            _uiState.value = UiState.Loading
            try {
                val state = _filterState.value
                val results = searchAnimeUseCase(
                    query = state.query,
                    genre = state.selectedGenre,
                    format = state.selectedFormat,
                    sort = state.selectedSort
                )
                _uiState.value = UiState.Success(results)
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _uiState.value = UiState.Error(e.message ?: "Error al buscar animes")
            }
        }
    }
}
