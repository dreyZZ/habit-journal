package com.tengri.habitexperiences.util

import android.app.Dialog
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.RelativeLayout
import com.github.chrisbanes.photoview.PhotoView
import com.tengri.habitexperiences.R


fun showImage(view: View, imageBytes: ByteArray) {
    val context = view.context

    val dialog = Dialog(context, R.style.DialogTheme)
    val window = dialog.window!!
//    dialog.window!!.setBackgroundDrawable(
//        ColorDrawable(Color.TRANSPARENT)
//    )
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    // initialize views
    val imageView = PhotoView(context)
    val bmp = convertByteArrayToBmp(imageBytes)

    val button = ImageButton(context)
    button.setImageResource(R.drawable.ic_baseline_delete_24)

    // add image view
    imageView.setImageBitmap(bmp)
    imageView.adjustViewBounds = true
    val imageParams = RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    imageParams.addRule(RelativeLayout.ALIGN_BOTTOM, button.id)

    window.addContentView(
        imageView, imageParams
    )

    // add image button
    val buttonParams = RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
    )
    buttonParams.addRule(RelativeLayout.ALIGN_BOTTOM, imageView.id)

    window.addContentView(button, buttonParams)

    dialog.show()
}