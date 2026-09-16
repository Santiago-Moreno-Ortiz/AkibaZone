package com.example.akibazone.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.akibazone.data.model.Anime as StoredAnime
import com.example.akibazone.data.repository.AnimeRepository
import com.example.akibazone.presentation.state.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ProfileStats(
    val favoriteCount: Int,
    val historyCount: Int
) {
    companion object {
        fun from(
            favorites: List<StoredAnime>,
            history: List<StoredAnime>
        ): ProfileStats = ProfileStats(
            favoriteCount = favorites.size,
            historyCount = history.size
        )
    }
}

class ProfileViewModel(private val repository: AnimeRepository) : ViewModel() {

    val statsState: StateFlow<UiState<ProfileStats>> = combine(
        repository.favorites.asFlow(),
        repository.history.asFlow(),
        ::toUiState
    )
        .catch { emit(UiState.Error("No se pudieron cargar las estadísticas.")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    private fun toUiState(
        favorites: List<StoredAnime>,
        history: List<StoredAnime>
    ): UiState<ProfileStats> = UiState.Success(ProfileStats.from(favorites, history))
}
