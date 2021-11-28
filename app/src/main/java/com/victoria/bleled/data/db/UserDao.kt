package com.victoria.bleled.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.victoria.bleled.data.model.ModelUser

@Dao
interface UserDao {
    @get:Query("SELECT * FROM tb_user")
    val all: List<ModelUser?>?

    @Query("SELECT * FROM tb_user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray?): List<ModelUser?>?

    @Query(
        "SELECT * FROM tb_user WHERE name LIKE :first LIMIT 1"
    )
    fun findByName(first: String?): ModelUser?

    @Insert
    fun insertAll(vararg users: ModelUser?)

    @Delete
    fun delete(user: ModelUser?)
}