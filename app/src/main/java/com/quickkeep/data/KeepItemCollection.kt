package com.quickkeep.data

import androidx.room.Entity

@Entity(primaryKeys = ["itemId", "collectionId"], tableName = "item_collection")
data class KeepItemCollection(
    val itemId: Long,
    val collectionId: Long
)
