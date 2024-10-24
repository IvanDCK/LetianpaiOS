package com.renhejia.robot.launcher.displaymode.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.Log
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.io.UnsupportedEncodingException
import java.security.MessageDigest

class GlideRoundTransform @JvmOverloads constructor(dp: Int = 4) :
    BitmapTransformation() {
    init {
        radius = Resources.getSystem().displayMetrics.density * dp
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        try {
            val keyBytes = "round".toByteArray(Charsets.UTF_8)
            messageDigest.update(keyBytes)
            // Add any other relevant parameters to the key
            // e.g., if you have a border:
            // messageDigest.update(borderWidth.toByteArray(Charsets.UTF_8))
            // messageDigest.update(borderColor.toByteArray(Charsets.UTF_8))
        } catch (e: UnsupportedEncodingException) {
            Log.e("GlideRoundTransform", "Couldn't update disk cache key", e)
        }
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap? {
        return roundCrop(pool, toTransform)
    }

    val id: String
        get() = javaClass.name + Math.round(radius)

    companion object {
        private var radius = 0f

        private fun roundCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
            if (source == null) return null

            var result = pool[source.width, source.height, Bitmap.Config.ARGB_8888]
            if (result == null) {
                result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
            }

            val canvas = Canvas(result)
            val paint = Paint()
            paint.setShader(
                BitmapShader(
                    source,
                    Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP
                )
            )
            paint.isAntiAlias = true
            val rectF = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat())
            canvas.drawRoundRect(rectF, radius, radius, paint)
            return result
        }
    }
}

