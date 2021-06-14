package com.tengri.habitjournal.dialogs

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.chrisbanes.photoview.PhotoView
import com.jsibbold.zoomage.ZoomageView
import com.tengri.habitjournal.R
import com.tengri.habitjournal.util.convertByteArrayToBmp

class ImageDialog(
    context: Context,
    private val imageBytes: ByteArray,
    private val onImageButtonClicked: () -> Unit
) : Dialog(context) {


    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_experience_image)

        val photoView = findViewById<ZoomageView>(R.id.imageView)
        val imageButton = findViewById<ImageButton>(R.id.imageButton)

        Glide.with(context)
            .load(imageBytes)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(ColorDrawable(Color.TRANSPARENT))
            .into(photoView)

//        val bmp = convertByteArrayToBmp(imageBytes)
//        photoView.setImageBitmap(bmp)

        when (context.resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                window!!.setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                photoView.layoutParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            else -> {
                window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                photoView.layoutParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }

        imageButton.setOnClickListener {
            dismiss()
            onImageButtonClicked()
        }
    }
}