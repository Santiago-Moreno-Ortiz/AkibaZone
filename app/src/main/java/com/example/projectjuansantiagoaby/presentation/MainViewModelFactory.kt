package com.example.projectjuansantiagoaby.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projectjuansantiagoaby.data.local.AppDatabase
import com.example.projectjuansantiagoaby.data.network.AnimeScraper
import com.example.projectjuansantiagoaby.data.network.NetworkModule
import com.example.projectjuansantiagoaby.data.repository.AnimeRepository
import com.example.projectjuansantiagoaby.presentation.home.HomeViewModel

import com.example.projectjuansantiagoaby.domain.usecase.GetAnimeDetailUseCase
import com.example.projectjuansantiagoaby.domain.usecase.GetHomeDataUseCase
import com.example.projectjuansantiagoaby.domain.usecase.SearchAnimeUseCase
import com.example.projectjuansantiagoaby.domain.usecase.ToggleFavoriteUseCase

class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    
    private val repository by lazy {
        val database = com.example.projectjuansantiagoaby.data.local.AppDatabase.getDatabase(application)
        com.example.projectjuansantiagoaby.data.repository.AnimeRepository(
            database.animeDao(),
            com.example.projectjuansantiagoaby.data.network.AnimeScraper(),
            com.example.projectjuansantiagoaby.data.network.NetworkModule.apiService,
            com.example.projectjuansantiagoaby.data.network.NetworkModule.anilistService
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> 
                HomeViewModel(GetHomeDataUseCase(repository)) as T
            modelClass.isAssignableFrom(com.example.projectjuansantiagoaby.presentation.anime.AnimeDetailViewModel::class.java) -> 
                com.example.projectjuansantiagoaby.presentation.anime.AnimeDetailViewModel(
                    GetAnimeDetailUseCase(repository),
                    ToggleFavoriteUseCase(repository)
                ) as T
            modelClass.isAssignableFrom(com.example.projectjuansantiagoaby.presentation.explore.ExploreViewModel::class.java) ->
                com.example.projectjuansantiagoaby.presentation.explore.ExploreViewModel(SearchAnimeUseCase(repository)) as T
            modelClass.isAssignableFrom(com.example.projectjuansantiagoaby.presentation.player.PlayerViewModel::class.java) ->
                com.example.projectjuansantiagoaby.presentation.player.PlayerViewModel(repository) as T
            modelClass.isAssignableFrom(com.example.projectjuansantiagoaby.presentation.profile.AuthViewModel::class.java) ->
                com.example.projectjuansantiagoaby.presentation.profile.AuthViewModel() as T
            modelClass.isAssignableFrom(com.example.projectjuansantiagoaby.presentation.favorites.FavoritesViewModel::class.java) ->
                com.example.projectjuansantiagoaby.presentation.favorites.FavoritesViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
