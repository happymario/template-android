package com.victoria.bleled.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.victoria.bleled.data.model.ModelUser;

//AppDatabase db = Room.databaseBuilder(getApplicationContext(),
//        AppDatabase.class, "database-name").build();
@Database(entities = {ModelUser.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}