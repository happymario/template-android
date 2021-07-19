package com.victoria.bleled.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.victoria.bleled.data.model.ModelUser;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM tb_user")
    List<ModelUser> getAll();

    @Query("SELECT * FROM tb_user WHERE uid IN (:userIds)")
    List<ModelUser> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM tb_user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    ModelUser findByName(String first, String last);

    @Insert
    void insertAll(ModelUser... users);

    @Delete
    void delete(ModelUser user);
}