package com.victoria.bleled.app.network.dagger

import com.victoria.bleled.data.remote.GithubService
import com.victoria.bleled.data.remote.adapter.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class GithubModule {
    @Provides
    fun provideGithubService(): GithubService {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(GithubService::class.java)
    }
}