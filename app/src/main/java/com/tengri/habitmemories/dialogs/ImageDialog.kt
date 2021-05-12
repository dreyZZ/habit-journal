package com.tengri.habitmemories.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import com.github.chrisbanes.photoview.PhotoView
import com.tengri.habitmemories.R
import com.tengri.habitmemories.util.convertByteArrayToBmp

class ImageDialog(context: Context, private val imageBytes: ByteArray) : Dialog(context) {


    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_memory_image)

        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val photoView = findViewById<PhotoView>(R.id.imageView)
        val bmp = convertByteArrayToBmp(imageBytes)
        photoView.setImageBitmap(bmp)

    }
}