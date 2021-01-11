package com.shareimages

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.shareimages.model.MyImage
import java.io.File

class ListFilesDirectory(private val context: Context) {
    fun getFiles(): ArrayList<MyImage>? {
        var images = arrayListOf<MyImage>()
        try {
            var files = File("${path()}")
            var listFiles = files.listFiles()
            var bitmap: Bitmap? = null

            for (file in listFiles) {
                Log.e("File", "${file.path}")
                bitmap = BitmapFactory.decodeFile("${file.path}")
                images.add(MyImage(icon = bitmap, file = file))
            }
            return images
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return arrayListOf()
    }

    private fun path(): File? {
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, context.resources.getString(R.string.app_name))
        }

        return if (mediaDir != null && mediaDir.exists())
            mediaDir else context.filesDir
    }
}
