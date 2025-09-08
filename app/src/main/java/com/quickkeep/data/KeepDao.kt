package com.quickkeep.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KeepDao {
    // items
    @Query("SELECT * FROM keep_items ORDER BY createdAt DESC")
    fun all(): Flow<List<KeepItem>>

    @Query("SELECT keep_items.* FROM keep_items INNER JOIN item_collection ic ON ic.itemId = keep_items.id WHERE ic.collectionId = :collectionId ORDER BY keep_items.createdAt DESC")
    fun byCollection(collectionId: Long): Flow<List<KeepItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: KeepItem): Long

    @Delete
    suspend fun delete(item: KeepItem)

    @Query("DELETE FROM keep_items WHERE id IN (:ids)")
    suspend fun deleteIds(ids: List<Long>)

    // collections
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCollection(collection: Collection): Long

    @Query("SELECT * FROM collections ORDER BY name ASC")
    fun collections(): Flow<List<Collection>>

    @Query("DELETE FROM collections WHERE id = :id")
    suspend fun deleteCollection(id: Long)

    // mapping
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun map(ic: KeepItemCollection)

    @Query("DELETE FROM item_collection WHERE itemId = :itemId")
    suspend fun clearMapping(itemId: Long)

    // counts for UI badges
    @Query("SELECT c.id AS id, c.name AS name, COUNT(ic.itemId) AS cnt FROM collections c LEFT JOIN item_collection ic ON c.id = ic.collectionId GROUP BY c.id ORDER BY c.name ASC")
    fun collectionCounts(): Flow<List<CollectionCount>>
}

data class CollectionCount(val id: Long, val name: String, val cnt: Int)
