package com.tengri.habitjournal.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


fun convertByteArrayToBmp(byteArray: ByteArray): Bitmap? {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}

fun convertDrawableToByteArray(resources: Resources, drawable: Int): ByteArray {
    val bitmap = BitmapFactory.decodeResource(resources, drawable)
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}

/**
 * @param dir you can get from many places like Environment.getExternalStorageDirectory() or mContext.getFilesDir() depending on where you want to save the image.
 * @param fileName The file name.
 * @param bm The Bitmap you want to save.
 * @param format Bitmap.CompressFormat can be PNG,JPEG or WEBP.
 * @param quality quality goes from 1 to 100. (Percentage).
 * @return true if the Bitmap was saved successfully, false otherwise.
 */
fun saveBitmapToFile(
    dir: File?, fileName: String, bm: Bitmap,
    format: CompressFormat?, quality: Int
): Boolean {
    val imageFile = File(dir, fileName)
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(imageFile)
        bm.compress(format, quality, fos)
        fos.close()
        return true
    } catch (e: IOException) {
        e.message?.let { Log.e("app", it) }
        if (fos != null) {
            try {
                fos.close()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
        }
    }
    return false
}