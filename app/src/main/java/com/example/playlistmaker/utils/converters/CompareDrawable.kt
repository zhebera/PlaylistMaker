package com.example.playlistmaker.utils.converters

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream
import java.util.*


fun <T : Drawable> T.bytesEqualTo(t: T?): Boolean = toBitmap().bytesEqualTo(t?.toBitmap(), true)

fun <T : Drawable> T.pixelsEqualTo(t: T?): Boolean = toBitmap().pixelsEqualTo(t?.toBitmap(), true)

fun Bitmap.bytesEqualTo(otherBitmap: Bitmap?, shouldRecycle: Boolean = false): Boolean = otherBitmap?.let { other ->
    if (width == other.width && height == other.height) {
        val res = toBytes().contentEquals(other.toBytes())
        if (shouldRecycle) {
            doRecycle().also { otherBitmap.doRecycle() }
        }
        res
    } else false
} ?: false

fun Bitmap.pixelsEqualTo(otherBitmap: Bitmap?, shouldRecycle: Boolean = false): Boolean = otherBitmap?.let { other ->
    if (width == other.width && height == other.height) {
        val res = Arrays.equals(toPixels(), other.toPixels())
        if (shouldRecycle) {
            doRecycle().also { otherBitmap.doRecycle() }
        }
        res
    } else false
} ?: false

fun Bitmap.doRecycle() {
    if (!isRecycled) recycle()
}

fun <T : Drawable> T.toBitmap(): Bitmap {
    if (this is BitmapDrawable) return bitmap

    val drawable: Drawable = this
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun Bitmap.toBytes(): ByteArray = ByteArrayOutputStream().use { stream ->
    compress(Bitmap.CompressFormat.JPEG, 100, stream)
    stream.toByteArray()
}

fun Bitmap.toPixels(): IntArray = IntArray(width * height).apply { getPixels(this, 0, width, 0, 0, width, height) }