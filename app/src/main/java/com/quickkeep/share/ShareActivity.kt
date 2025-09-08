package com.quickkeep.share

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.quickkeep.data.*
import kotlinx.coroutines.*

class ShareActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleShare()
    }

    private fun handleShare() {
        val dao = AppDb.get(this).keepDao()
        val action = intent?.action
        val type = intent?.type ?: ""

        CoroutineScope(Dispatchers.IO).launch {
            var savedId: Long? = null
            when {
                Intent.ACTION_SEND == action && type.startsWith("text/") -> {
                    val text = intent.getStringExtra(Intent.EXTRA_TEXT)
                    val subject = intent.getStringExtra(Intent.EXTRA_SUBJECT)
                    val url = extractUrl(text)
                    savedId = dao.insert(KeepItem(title = subject ?: url, content = text, url = url, imageUri = null, sourceApp = callingPackage))
                }
                Intent.ACTION_SEND == action && type.startsWith("image/") -> {
                    val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                    savedId = dao.insert(KeepItem(title = intent.getStringExtra(Intent.EXTRA_SUBJECT), content = intent.getStringExtra(Intent.EXTRA_TEXT), url = null, imageUri = uri?.toString(), sourceApp = callingPackage))
                }
                Intent.ACTION_SEND_MULTIPLE == action && type.startsWith("image/") -> {
                    val uris = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM).orEmpty()
                    var firstId: Long? = null
                    uris.forEachIndexed { idx, u ->
                        val id = dao.insert(KeepItem(title = intent.getStringExtra(Intent.EXTRA_SUBJECT), content = intent.getStringExtra(Intent.EXTRA_TEXT), url = null, imageUri = u.toString(), sourceApp = callingPackage))
                        if (idx == 0) firstId = id
                    }
                    savedId = firstId
                }
            }

            withContext(Dispatchers.Main) {
                savedId?.let { id ->
                    val pick = Intent(this@ShareActivity, SelectCollectionActivity::class.java).apply { putExtra("itemId", id) }
                    startActivity(pick)
                } ?: run {
                    Toast.makeText(this@ShareActivity, "Nothing to save", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }
    }

    private fun extractUrl(text: String?): String? = text?.let { "(https?://\S+)".toRegex().find(it)?.value }
}
