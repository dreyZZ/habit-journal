package com.tengri.habitmemories.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.chrisbanes.photoview.PhotoView
import com.tengri.habitmemories.R

class ImageDialog(context: Context, private val imageBytes: ByteArray) : Dialog(context) {


    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_memory_image)

        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val photoView = findViewById<PhotoView>(R.id.imageView)

        Glide.with(context)
            .load(imageBytes)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(photoView)

    }
}