package com.example.projectjuansantiagoaby.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projectjuansantiagoaby.data.local.AppDatabase
import com.example.projectjuansantiagoaby.data.network.AnimeScraper
import com.example.projectjuansantiagoaby.data.network.NetworkModule
import com.example.projectjuansantiagoaby.data.repository.AnimeRepository
import com.example.projectjuansantiagoaby.presentation.home.HomeViewModel

class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    
    private val repository by lazy {
        val database = AppDatabase.getDatabase(application)
        com.example.projectjuansantiagoaby.data.repository.AnimeRepository(
            database.animeDao(),
            com.example.projectjuansantiagoaby.data.network.AnimeScraper(),
            com.example.projectjuansantiagoaby.data.network.NetworkModule.apiService,
            com.example.projectjuansantiagoaby.data.network.NetworkModule.anilistService
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository) as T
            modelClass.isAssignableFrom(com.example.projectjuansantiagoaby.presentation.anime.AnimeDetailViewModel::class.java) -> 
                com.example.projectjuansantiagoaby.presentation.anime.AnimeDetailViewModel(repository) as T
            modelClass.isAssignableFrom(com.example.projectjuansantiagoaby.presentation.explore.ExploreViewModel::class.java) ->
                com.example.projectjuansantiagoaby.presentation.explore.ExploreViewModel(repository) as T
            modelClass.isAssignableFrom(com.example.projectjuansantiagoaby.presentation.player.PlayerViewModel::class.java) ->
                com.example.projectjuansantiagoaby.presentation.player.PlayerViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
