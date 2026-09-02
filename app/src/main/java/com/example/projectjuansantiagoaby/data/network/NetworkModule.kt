package com.example.projectjuansantiagoaby.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "https://jimov.herokuapp.com/"
    private const val ANILIST_URL = "https://graphql.anilist.co/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val anilistRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ANILIST_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: JimoApiService by lazy {
        retrofit.create(JimoApiService::class.java)
    }

    val anilistService: AnilistApiService by lazy {
        anilistRetrofit.create(AnilistApiService::class.java)
    }
}
