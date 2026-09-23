package com.example.akibazone.presentation.player

sealed interface PlayerInput {
    data class Episode(val id: String) : PlayerInput
    data object Demo : PlayerInput
}
