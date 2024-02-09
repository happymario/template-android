package com.mario.template.di

import com.mario.template.data.local.datastore.PreferenceStorage
import com.mario.template.data.local.room.HistorySearchAddressDao
import com.mario.template.data.remote.apiservice.MyWeatherApiService
import com.mario.template.data.remote.apiservice.OneCallApiService
import com.mario.template.data.repository.LocalRepository
import com.mario.template.data.repository.LocalRepositoryImpl
import com.mario.template.data.repository.WeatherRepository
import com.mario.template.data.repository.WeatherRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideWeatherRepository(
        currentWeatherApiService: MyWeatherApiService,
        oneCallApiService: OneCallApiService,
    ): WeatherRepository = WeatherRepositoryImpl(currentWeatherApiService, oneCallApiService)

    @Provides
    fun provideLocalRepository(
        preferenceStorage: PreferenceStorage, searchAddressDao: HistorySearchAddressDao,
    ): LocalRepository = LocalRepositoryImpl(preferenceStorage, searchAddressDao)
}
