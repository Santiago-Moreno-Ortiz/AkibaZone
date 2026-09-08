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
import com.example.projectjuansantiagoaby.presentation.state.UiState

class HomeViewModel(private val getHomeDataUseCase: GetHomeDataUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeData>> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val data = getHomeDataUseCase()
                _uiState.value = UiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al cargar datos")
            }
        }
    }
}
