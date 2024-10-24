package com.renhejia.robot.launcher.displaymode.utils

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.util.Log
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import kotlin.math.min

class GlideCircleTransform : BitmapTransformation() {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        try {
            val keyBytes = "circle".toByteArray(Charsets.UTF_8)
            messageDigest.update(keyBytes)
            // Add any other relevant parameters to the key
            // e.g., if you have a border:
            // messageDigest.update(borderWidth.toByteArray(Charsets.UTF_8))
            // messageDigest.update(borderColor.toByteArray(Charsets.UTF_8))
        } catch (e: UnsupportedEncodingException) {
            Log.e("GlideCircleTransform", "Couldn't update disk cache key", e)
        }
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap? {
        return circleCrop(pool, toTransform)
    }

    val id: String
        get() = javaClass.name

    companion object {
        private fun circleCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
            if (source == null) return null

            val size =
                min(source.width.toDouble(), source.height.toDouble()).toInt()
            val x = (source.width - size) / 2
            val y = (source.height - size) / 2

            val squared = Bitmap.createBitmap(source, x, y, size, size)

            var result = pool[size, size, Bitmap.Config.ARGB_8888]
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            }

            val canvas = Canvas(result)
            val paint = Paint()
            paint.setShader(
                BitmapShader(
                    squared,
                    Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP
                )
            )
            paint.isAntiAlias = true
            val r = size / 2f
            canvas.drawCircle(r, r, r, paint)
            return result
        }
    }
}
