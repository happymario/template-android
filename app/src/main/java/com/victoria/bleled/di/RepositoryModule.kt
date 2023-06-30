package com.victoria.bleled.di

import android.content.Context
import com.victoria.bleled.data.net.github.IGithubService
import com.victoria.bleled.data.net.mytemplate.IMyTemplateService
import com.victoria.bleled.data.repository.DataStoreRepository
import com.victoria.bleled.data.repository.DataStoreRepositoryImpl
import com.victoria.bleled.data.repository.GithubSearchRepository
import com.victoria.bleled.data.repository.MyTemplateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun provideDataStoreRepository(@ApplicationContext app: Context): DataStoreRepository =
        DataStoreRepositoryImpl(app)

    @Singleton
    @Provides
    fun provideGithubSearchRepository(@NetworkModule.GithubApiServer githubService: IGithubService): GithubSearchRepository =
        GithubSearchRepository(githubService)

    @Singleton
    @Provides
    fun provideTemplateRepository(@NetworkModule.MyApiServer apiService: IMyTemplateService): MyTemplateRepository =
        MyTemplateRepository(apiService)
}