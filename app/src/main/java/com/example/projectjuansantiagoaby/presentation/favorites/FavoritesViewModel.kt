package com.example.projectjuansantiagoaby.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.projectjuansantiagoaby.data.repository.AnimeRepository
import com.example.projectjuansantiagoaby.data.repository.toDomain
import com.example.projectjuansantiagoaby.domain.model.Anime
import com.example.projectjuansantiagoaby.presentation.state.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(private val repository: AnimeRepository) : ViewModel() {

    val favoritesState: StateFlow<UiState<List<Anime>>> = repository.favorites.asFlow()
        .map { list -> 
            if (list.isEmpty()) UiState.Success(emptyList()) 
            else UiState.Success(list.map { it.toDomain() }) 
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)
}
