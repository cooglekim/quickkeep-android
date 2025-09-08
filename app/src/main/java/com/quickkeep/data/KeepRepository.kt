package com.quickkeep.data

import kotlinx.coroutines.flow.first

class KeepRepository(private val dao: KeepDao) {
    fun all() = dao.all()
    fun byCollection(collectionId: Long) = dao.byCollection(collectionId)
    fun collections() = dao.collections()
    fun collectionCounts() = dao.collectionCounts()

    suspend fun insert(item: KeepItem) = dao.insert(item)
    suspend fun delete(item: KeepItem) = dao.delete(item)
    suspend fun deleteIds(ids: List<Long>) = dao.deleteIds(ids)

    suspend fun ensureCollection(name: String): Long {
        val id = dao.insertCollection(Collection(name = name))
        return if (id == -1L) {
            val list = dao.collections().first()
            list.first { it.name == name }.id
        } else id
    }

    suspend fun mapItemToCollections(itemId: Long, collectionIds: List<Long>) {
        dao.clearMapping(itemId)
        collectionIds.forEach { dao.map(KeepItemCollection(itemId, it)) }
    }
}
