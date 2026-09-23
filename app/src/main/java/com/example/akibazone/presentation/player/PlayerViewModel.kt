package com.example.akibazone.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.akibazone.data.network.PlaybackSource
import com.example.akibazone.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PlayerUiState {
    object Loading : PlayerUiState
    data class Ready(val source: PlaybackSource, val demo: DemoVideo? = null) : PlayerUiState
    object NoSource : PlayerUiState
    data class Error(val message: String) : PlayerUiState
}

class PlayerViewModel(private val repository: AnimeRepository? = null) : ViewModel() {
    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun onPlaybackError() {
        _uiState.value = PlayerUiState.Error("No se pudo reproducir el enlace de video.")
    }

    fun loadDemo(video: DemoVideo?) {
        _uiState.value = if (video == null) {
            PlayerUiState.Error("Video de demostración no disponible. Agrega el MP4 autorizado y vuelve a compilar la aplicación.")
        } else {
            PlayerUiState.Ready(
                PlaybackSource(video.uri, com.example.akibazone.data.network.PlaybackFormat.MP4),
                demo = video
            )
        }
    }

    fun loadVideo(episodeId: String) {
        viewModelScope.launch {
            _uiState.value = PlayerUiState.Loading
            try {
                val sources = requireNotNull(repository) { "Repositorio de reproducción no disponible" }.getPlaybackSources(episodeId)
                if (sources.isNotEmpty()) {
                    _uiState.value = PlayerUiState.Ready(sources.first())
                } else {
                    _uiState.value = PlayerUiState.NoSource
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _uiState.value = PlayerUiState.Error(e.message ?: "Error")
            }
        }
    }
}
