package com.mario.template.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mario.template.base.BaseModel

@Entity(tableName = "history_search_address")
data class HistorySearchAddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val addressName: String,
    val timeSearch: Long,
) : BaseModel()
