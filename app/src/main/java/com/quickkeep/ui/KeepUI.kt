package com.quickkeep.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.quickkeep.data.KeepItem

@Composable
fun KeepHome(vm: MainViewModel) {
    val ui by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("QuickKeep") }, actions = {
                TextButton(onClick = { vm.exportCsv() }) { Text("CSV") }
                TextButton(onClick = { vm.exportZipLauncher() }) { Text("백업") }
                TextButton(onClick = { vm.importZipLauncher() }) { Text("복원") }
            })
        }
    ) { pad ->
        Column(Modifier.padding(pad)) {
            ScrollableTabRow(selectedTabIndex = ui.selectedTabIndex) {
                Tab(selected = ui.selectedTabIndex == 0, onClick = { vm.selectAll() }, text = { Text("전체") })
                ui.collections.forEachIndexed { idx, c ->
                    Tab(selected = ui.selectedTabIndex == idx + 1, onClick = { vm.selectCollection(c.id) }, text = { Text("${c.name} ${c.cnt}") })
                }
                Tab(selected = false, onClick = { vm.promptCreateCollection() }, text = { Text("+ 새 컬렉션") })
            }
            LazyColumn(Modifier.padding(12.dp)) {
                items(ui.items, key = { it.id }) { item -> KeepCard(item = item, onShare = { vm.share(item) }) }
            }
        }
    }
}

@Composable
fun KeepCard(item: KeepItem, onShare: () -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(item.title ?: item.url ?: "(제목 없음)", style = MaterialTheme.typography.titleMedium)
            if (!item.imageUri.isNullOrBlank()) { AsyncImage(model = item.imageUri, contentDescription = null, modifier = Modifier.fillMaxWidth().height(180.dp)) }
            if (!item.url.isNullOrBlank()) Text(item.url!!)
            if (!item.content.isNullOrBlank()) Text(item.content!!, style = MaterialTheme.typography.bodySmall)
            Row { TextButton(onClick = onShare) { Text("공유") } }
        }
    }
}
