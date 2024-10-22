package com.renhejia.robot.expression.face

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.LruCache
import java.io.InputStream

/**
 * 动画中所使用的图片的内存缓存
 * @author liujunbin
 */
class AnimationLruCache private constructor(context: Context?) {
    private var mContext: Context? = null
    private var mCache: LruCache<String, PropertyFrameDrawable>? = null

    val MAX_SIZE: Int = 60 * 1024 * 1024

    init {
        mContext = context
        mCache = object : LruCache<String, PropertyFrameDrawable>(MAX_SIZE) {

        override fun entryRemoved(evicted: Boolean, key: String, oldValue: PropertyFrameDrawable?, newValue: PropertyFrameDrawable?) {
            if (oldValue is BitmapDrawable) {
                val bitmapDrawable = oldValue as BitmapDrawable
                bitmapDrawable.bitmap.recycle() // Recycle the bitmap to free memory
            }
        }

            override fun sizeOf(key: String?, value: PropertyFrameDrawable?): Int {
                return getDrawableByteCount(value)
            }
        }
    }

    /**
     * 从缓存中得到图片，如果内存中不存在则从硬件加载
     *
     * @param path asset中全路径
     * @return
     */
    fun getDrawable(path: String): PropertyFrameDrawable? {
        val d = mCache!![path]
        if (d != null) {
            return d
        }
        val `is`: InputStream
        try {
            `is` = mContext!!.assets.open(path)
            val drawable = PropertyFrameDrawable(
                mContext!!.resources, `is`
            )
            mCache!!.put(path, drawable)
            return drawable
        } catch (e: Throwable) {
            return null
        }
    }

    /**
     * 清空所有
     */
    fun clear() {
        mCache!!.evictAll()
    }

    private fun getDrawableByteCount(drawable: Drawable?): Int {
        if (drawable is BitmapDrawable) {
            val bm = drawable.bitmap
            return bm.height * bm.width * 4
        }
        return 0
    }

    companion object {
        const val MAX_SIZE: Int = 60 * 1024 * 1024

        private var lruCache: AnimationLruCache? = null
        fun getInstance(context: Context?): AnimationLruCache {
            if (lruCache == null) {
                lruCache = AnimationLruCache(context)
            }
            return lruCache!!
        }
    }
}
