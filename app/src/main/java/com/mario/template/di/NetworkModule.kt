package com.mario.template.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mario.template.data.remote.apiservice.GithubService
import com.mario.template.data.remote.apiservice.MyTemplateApiService
import com.mario.template.data.remote.apiservice.OneCallApiService
import com.mario.template.data.remote.calladapter.coroutines.CoroutinesResponseCallAdapterFactory
import com.mario.template.data.remote.calladapter.flow.FlowCallAdapterFactory
import com.mario.template.data.remote.interceptor.HeaderInterceptor
import com.victoria.bleled.util.thirdparty.gson.GsonDateAdapter
import com.victoria.bleled.util.thirdparty.gson.GsonDateTimeAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TIME_OUT = 1L

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MyApiServer

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GithubApiServer


    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, GsonDateTimeAdapter())
        .registerTypeAdapter(LocalDate::class.java, GsonDateAdapter())
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideLogger(gson: Gson) = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideHttpClient(headerInterceptor: HeaderInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .callTimeout(TIME_OUT, TimeUnit.MINUTES)
            .connectTimeout(TIME_OUT, TimeUnit.MINUTES)
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(headerInterceptor)
            .build()

    @GithubApiServer
    @Provides
    fun provideBaseGithubApiUrl() = GithubService.BASE_URL

    @GithubApiServer
    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, @GithubApiServer baseApiUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseApiUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            //.addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
            .build()
    }

    @GithubApiServer
    @Provides
    @Singleton
    fun provideGithubService(@GithubApiServer retrofit: Retrofit) =
        retrofit.create(OneCallApiService::class.java)

    @MyApiServer
    @Singleton
    @Provides
    fun provideRetrofit2(
        client: OkHttpClient
    ): Retrofit {

        return Retrofit.Builder()
            .baseUrl(MyTemplateApiService.BASE_URL + "api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(FlowCallAdapterFactory())
            .build()
    }

    @MyApiServer
    @Provides
    @Singleton
    fun provideTemplateService(@MyApiServer retrofit: Retrofit) =
        retrofit.create(MyTemplateApiService::class.java)
}
