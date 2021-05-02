package com.tengri.habitmemories.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun convertByteArrayToBmp(byteArray: ByteArray): Bitmap? {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}