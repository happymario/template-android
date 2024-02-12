package com.mario.template.di

import android.content.Context
import androidx.room.Room
import com.mario.template.data.local.room.HistorySearchAddressDao
import com.mario.template.data.local.room.MyTemplateDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideMyTemplateDatabase(
        @ApplicationContext context: Context,
    ): MyTemplateDatabase =
        Room.databaseBuilder(
            context,
            MyTemplateDatabase::class.java,
            MyTemplateDatabase.database_name
        )
            .build()

    @Provides
    @Singleton
    fun provideSearchTemplateDao(
        mytemplateDatabase: MyTemplateDatabase
    ): HistorySearchAddressDao = mytemplateDatabase.historySearchAddressDao()
}
