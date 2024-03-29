package com.mario.template.di

import com.mario.template.data.local.datastore.PreferenceStorage
import com.mario.template.data.local.room.HistorySearchAddressDao
import com.mario.template.data.remote.apiservice.MyTemplateApiService
import com.mario.template.data.remote.apiservice.OneCallApiService
import com.mario.template.data.repository.LocalRepository
import com.mario.template.data.repository.LocalRepositoryImpl
import com.mario.template.data.repository.TemplateRepository
import com.mario.template.data.repository.TemplateRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideTemplateRepository(
        @NetworkModule.MyApiServer currentTemplateApiService: MyTemplateApiService,
        @NetworkModule.GithubApiServer oneCallApiService: OneCallApiService,
    ): TemplateRepository = TemplateRepositoryImpl(currentTemplateApiService, oneCallApiService)

    @Provides
    fun provideLocalRepository(
        preferenceStorage: PreferenceStorage, searchAddressDao: HistorySearchAddressDao,
    ): LocalRepository = LocalRepositoryImpl(preferenceStorage, searchAddressDao)
}
