package com.quickkeep.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keep_items")
data class KeepItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String?,
    val content: String?,
    val url: String?,
    val imageUri: String?,
    val sourceApp: String?,
    val createdAt: Long = System.currentTimeMillis()
)
