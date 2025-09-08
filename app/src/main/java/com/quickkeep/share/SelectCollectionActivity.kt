package com.quickkeep.share

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.quickkeep.ui.SelectCollectionViewModel
import com.quickkeep.ui.SelectCollectionScreen

class SelectCollectionActivity : ComponentActivity() {
    private val vm: SelectCollectionViewModel by viewModels { SelectCollectionViewModel.factory(applicationContext) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemId = intent.getLongExtra("itemId", -1)
        setContent { MaterialTheme { SelectCollectionScreen(vm, itemId) { finish() } } }
    }
}
