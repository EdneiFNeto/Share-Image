package com.shareimages.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shareimages.ListFilesDirectory
import com.shareimages.R
import com.shareimages.adapter.GaleryAdapter
import com.shareimages.model.MyImage
import kotlinx.android.synthetic.main.activity_galery.*
import kotlinx.android.synthetic.main.activity_galery.topAppBar

class GaleryActivity : AppCompatActivity() {

    private val imagens = ArrayList<MyImage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_galery)
    }

    override fun onResume() {
        super.onResume()
        initAdapter()
        initEventListener()
    }

    private fun initEventListener() {
        topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initAdapter() {
        val galeryAdapter = GaleryAdapter(this, imagens)
        recyle_view.apply {
            adapter = galeryAdapter
        }

        var listImages = ListFilesDirectory(this).getFiles()
        if (listImages?.isNotEmpty() == true) {
            listImages?.forEach {
                if (it != null)
                    imagens.add(it)
            }
        }

        galeryAdapter.notifyDataSetChanged()
    }
}