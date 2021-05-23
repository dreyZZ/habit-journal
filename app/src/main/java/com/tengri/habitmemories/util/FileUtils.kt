package com.tengri.habitmemories.util

import android.R
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream


fun convertByteArrayToBmp(byteArray: ByteArray): Bitmap? {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}

fun convertDrawableToByteArray(resources: Resources, drawable: Int): ByteArray {
    val bitmap = BitmapFactory.decodeResource(resources, drawable)
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}