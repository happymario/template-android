package com.victoria.bleled.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.victoria.bleled.data.net.adapter.CoroutinesResponseCallAdapterFactory
import com.victoria.bleled.data.net.adapter.live.LiveDataCallAdapterFactory
import com.victoria.bleled.data.net.github.IGithubService
import com.victoria.bleled.data.net.mytemplate.IMyTemplateService
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
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MyApiServer

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GithubApiServer

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ApiOkHttpClient


    @RequiresApi(Build.VERSION_CODES.O)
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

    @ApiOkHttpClient
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

    @GithubApiServer
    @Provides
    fun provideBaseGithubApiUrl() = IGithubService.API_BASE_URL
    @GithubApiServer
    @Singleton
    @Provides
    fun provideRetrofit(@ApiOkHttpClient client: OkHttpClient, @GithubApiServer baseApiUrl: String): Retrofit {
        return RetrofitHelper.createRetrofit(baseApiUrl, CoroutinesResponseCallAdapterFactory())
    }

    @GithubApiServer
    @Provides
    @Singleton
    fun provideGithubService(@GithubApiServer retrofit: Retrofit) = retrofit.create(IGithubService::class.java)

    @MyApiServer
    @Provides
    fun provideBaseMyApiUrl() = IMyTemplateService.API_BASE_URL

    @MyApiServer
    @Singleton
    @Provides
    fun provideRetrofit2(
        @ApiOkHttpClient client: OkHttpClient,
        @MyApiServer baseApiUrl: String
    ): Retrofit {

        return RetrofitHelper.createRetrofit(
            IMyTemplateService.API_BASE_URL,
            LiveDataCallAdapterFactory(),
            IMyTemplateService.getCommonParams()
        )
    }

    @MyApiServer
    @Provides
    @Singleton
    fun provideTemplateService(@MyApiServer retrofit: Retrofit) = retrofit.create(IMyTemplateService::class.java)
}