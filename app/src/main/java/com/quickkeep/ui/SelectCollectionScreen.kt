package com.quickkeep.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SelectCollectionScreen(vm: SelectCollectionViewModel, itemId: Long, onDone: () -> Unit) {
    var newName by remember { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar(title = { Text("Keep 저장") }) }, bottomBar = {
        Row(Modifier.padding(12.dp)) { Text("항목을 저장할 컬렉션을 선택하거나 새로 만드세요.") }
    }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("+ 새 컬렉션 만들기") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    if (newName.isNotBlank()) { vm.createAndMap(itemId, newName.trim()); onDone() }
                }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            val counts = vm.counts.collectAsState().value
            counts.forEach { c ->
                ListItem(
                    headlineContent = { Text(c.name) },
                    supportingContent = { Text("${c.cnt}") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingContent = {
                        TextButton(onClick = { vm.map(itemId, c.id); onDone() }) { Text("저장") }
                    }
                )
                Divider()
            }
        }
    }
}
