package com.shareimages

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

class SharedImage(private val context: Context, private val file: File) {
    fun shared(){
        try {
            val uri: Uri? = getUri()
            if (uri != null) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "*/*"
                    putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file))
                }
                try {
                    context.startActivity(Intent.createChooser(intent, "Compartilhar"))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getUri(): Uri? {
        return try {
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )
        } catch (e: IllegalArgumentException) {
            Log.e("File Selector", "The selected file can't be shared: $file")
            null
        }
    }


}
