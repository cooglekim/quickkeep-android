package com.quickkeep.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quickkeep.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(private val app: Context, private val repo: KeepRepository) : ViewModel() {
    data class UiState(
        val items: List<KeepItem> = emptyList(),
        val collections: List<CollectionCount> = emptyList(),
        val selectedCollectionId: Long? = null,
        val selectedTabIndex: Int = 0
    )

    private val selected = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<UiState> = combine(
        selected,
        repo.collectionCounts(),
        selected.flatMapLatest { id -> if (id == null) repo.all() else repo.byCollection(id) }
    ) { sel, counts, items ->
        UiState(
            items = items,
            collections = counts,
            selectedCollectionId = sel,
            selectedTabIndex = if (sel == null) 0 else (counts.indexOfFirst { it.id == sel } + 1).coerceAtLeast(1)
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    fun selectAll() { selected.value = null }
    fun selectCollection(id: Long) { selected.value = id }

    fun promptCreateCollection() { /* TODO: optional name dialog */ }

    fun share(item: KeepItem) {
        val text = listOfNotNull(item.title, item.url, item.content).distinct().joinToString("\n")
        val intent = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, text); addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        app.startActivity(Intent.createChooser(intent, "공유"))
    }

    fun exportCsv() { /* TODO */ }
    fun exportZipLauncher() { /* TODO: SAF launcher */ }
    fun importZipLauncher() { /* TODO */ }

    companion object {
        fun factory(appContext: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDb.get(appContext)
                val repo = KeepRepository(db.keepDao())
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(appContext, repo) as T
            }
        }
    }
}
