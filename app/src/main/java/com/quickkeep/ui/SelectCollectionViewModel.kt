package com.quickkeep.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quickkeep.data.AppDb
import com.quickkeep.data.KeepRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SelectCollectionViewModel(private val app: Context, private val repo: KeepRepository) : ViewModel() {
    val counts = repo.collectionCounts().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun createAndMap(itemId: Long, name: String) = viewModelScope.launch {
        val id = repo.ensureCollection(name)
        repo.mapItemToCollections(itemId, listOf(id))
    }

    fun map(itemId: Long, collectionId: Long) = viewModelScope.launch {
        repo.mapItemToCollections(itemId, listOf(collectionId))
    }

    companion object {
        fun factory(app: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDb.get(app)
                val repo = KeepRepository(db.keepDao())
                @Suppress("UNCHECKED_CAST")
                return SelectCollectionViewModel(app, repo) as T
            }
        }
    }
}
