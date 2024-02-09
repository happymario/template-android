package com.mario.template.di

import com.mario.template.Constants
import com.mario.template.data.remote.apiservice.MyWeatherApiService
import com.mario.template.data.remote.apiservice.OneCallApiService
import com.mario.template.data.remote.calladapter.FlowCallAdapterFactory
import com.mario.template.data.remote.interceptor.HeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideOkHttpClient(
        headerInterceptor: HeaderInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .callTimeout(TIME_OUT, TimeUnit.MINUTES)
        .connectTimeout(TIME_OUT, TimeUnit.MINUTES)
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor(headerInterceptor)
        .build()

    @Provides
    fun provideRetrofit(
        flowCallAdapterFactory: FlowCallAdapterFactory,
        okHttpClient: OkHttpClient,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL + "api/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(flowCallAdapterFactory)
        .build()

    @Provides
    fun provideOneCallApiService(retrofit: Retrofit): OneCallApiService =
        retrofit.create(OneCallApiService::class.java)

    @Provides
    fun provideCurrentWeatherApiService(retrofit: Retrofit): MyWeatherApiService =
        retrofit.create(MyWeatherApiService::class.java)

    private const val TIME_OUT = 1L
}
