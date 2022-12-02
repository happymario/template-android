package com.victoria.bleled.app.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.victoria.bleled.data.net.adapter.CoroutinesResponseCallAdapterFactory
import com.victoria.bleled.data.net.github.IGithubService
import com.victoria.bleled.util.thirdparty.gson.DateAdapter
import com.victoria.bleled.util.thirdparty.gson.DateTimeAdapter
import com.victoria.bleled.util.thirdparty.retrofit.RetrofitHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, DateTimeAdapter())
        .registerTypeAdapter(LocalDate::class.java, DateAdapter())
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideLogger(gson: Gson) = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideHttpClient(logger: HttpLoggingInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()

//        builder.addInterceptor { chain: Interceptor.Chain ->
//            if (!AppPrefs.apiKey.isNullOrBlank()) {
//                chain.proceed(
//                    chain.request().newBuilder()
//                        .addHeader(AUTHORIZATION_NAME, AppPrefs.apiKey ?: "")
//                        .build()
//                )
//            } else {
//                chain.proceed(chain.request())
//            }
//        }

        builder.addInterceptor(logger)
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, httpClient: OkHttpClient) = RetrofitHelper.createRetrofit(
        IGithubService.API_BASE_URL, CoroutinesResponseCallAdapterFactory())

    @Provides
    @Singleton
    fun provideIGithubService(retrofit: Retrofit) = retrofit.create(IGithubService::class.java)

    companion object {
        const val AUTHORIZATION_NAME = "sessionKey"
        // const val AUTHORIZATION_TYPE = "Bearer"
    }
}