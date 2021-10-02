package com.victoria.bleled.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.victoria.bleled.data.model.ModelUser

@Database(entities = [ModelUser::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        var NAME = "AppDatabase"
        private var instance: AppDatabase? = null

        @Synchronized
        fun get(context: Context): AppDatabase {
            if (instance == null) {
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, NAME
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            fillInDb(context.applicationContext)
                        }
                    })
                instance = builder.build()
            }
            return instance!!
        }

        fun clear(context: Context) {
            instance?.close()
            instance = null
            context.deleteDatabase(NAME)
        }

        /**
         * fill database with list of cheeses
         */
        private fun fillInDb(context: Context) {
            // inserts in Room are executed on the current thread, so we insert in the background
//            ioThread {
//                get(context).cheeseDao().insert(
//                    CHEESE_DATA.map { Cheese(id = 0, name = it) })
//            }
        }
    }
}