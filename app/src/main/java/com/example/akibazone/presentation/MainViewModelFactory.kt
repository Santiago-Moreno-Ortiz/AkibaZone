package com.example.akibazone.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.akibazone.data.local.AppDatabase
import com.example.akibazone.data.network.AnimeScraper
import com.example.akibazone.data.network.NetworkModule
import com.example.akibazone.data.repository.AnimeRepository
import com.example.akibazone.presentation.home.HomeViewModel

import com.example.akibazone.domain.usecase.GetAnimeDetailUseCase
import com.example.akibazone.domain.usecase.GetHomeDataUseCase
import com.example.akibazone.domain.usecase.SearchAnimeUseCase
import com.example.akibazone.domain.usecase.ToggleFavoriteUseCase

class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    
    private val repository by lazy {
        val database = com.example.akibazone.data.local.AppDatabase.getDatabase(application)
        com.example.akibazone.data.repository.AnimeRepository(
            database.animeDao(),
            com.example.akibazone.data.network.AnimeScraper(),
            com.example.akibazone.data.network.NetworkModule.anilistService,
            com.example.akibazone.data.network.NetworkModule.jikanService
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> 
                HomeViewModel(GetHomeDataUseCase(repository), SearchAnimeUseCase(repository)) as T
            modelClass.isAssignableFrom(com.example.akibazone.presentation.anime.AnimeDetailViewModel::class.java) -> 
                com.example.akibazone.presentation.anime.AnimeDetailViewModel(
                    GetAnimeDetailUseCase(repository),
                    ToggleFavoriteUseCase(repository)
                ) as T
            modelClass.isAssignableFrom(com.example.akibazone.presentation.explore.ExploreViewModel::class.java) ->
                com.example.akibazone.presentation.explore.ExploreViewModel(SearchAnimeUseCase(repository)) as T
            modelClass.isAssignableFrom(com.example.akibazone.presentation.player.PlayerViewModel::class.java) ->
                com.example.akibazone.presentation.player.PlayerViewModel(repository) as T
            modelClass.isAssignableFrom(com.example.akibazone.presentation.profile.AuthViewModel::class.java) ->
                com.example.akibazone.presentation.profile.AuthViewModel() as T
            modelClass.isAssignableFrom(com.example.akibazone.presentation.favorites.FavoritesViewModel::class.java) ->
                com.example.akibazone.presentation.favorites.FavoritesViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
