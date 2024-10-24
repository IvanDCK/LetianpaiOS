package com.renhejia.robot.guidelib.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.max

class RoundImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    //圆角大小，默认为10
    private val mBorderRadius: Int = 100

    private val mPaint: Paint

    // 3x3 矩阵，主要用于缩小放大
    private val mMatrix: Matrix

    //渲染图像，使用图像为绘制图形着色
    private var mBitmapShader: BitmapShader? = null

    init {
        mMatrix = Matrix()
        mPaint = Paint()
        mPaint.setAntiAlias(true)
    }

    override fun onDraw(canvas: Canvas) {
        if (getDrawable() == null) {
            return
        }
        val bitmap: Bitmap = drawableToBitamp(getDrawable())
        mBitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        var scale: Float = 1.0f
        if (!(bitmap.getWidth() == getWidth() && bitmap.getHeight() == getHeight())) {
            // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
            scale = max(
                (getWidth() * 1.0f / bitmap.getWidth()).toDouble(),
                (getHeight() * 1.0f / bitmap.getHeight()).toDouble()
            ).toFloat()
        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale)
        // 设置变换矩阵
        mBitmapShader!!.setLocalMatrix(mMatrix)
        // 设置shader
        mPaint.setShader(mBitmapShader)
        canvas.drawRoundRect(
            RectF(0f, 0f, getWidth().toFloat(), getHeight().toFloat()),
            mBorderRadius.toFloat(),
            mBorderRadius.toFloat(),
            mPaint
        )
    }


    private fun drawableToBitamp(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.getBitmap()
        }
        // 当设置不为图片，为颜色时，获取的drawable宽高会有问题，所有当为颜色时候获取控件的宽高
        val w: Int =
            if (drawable.getIntrinsicWidth() <= 0) getWidth() else drawable.getIntrinsicWidth()
        val h: Int =
            if (drawable.getIntrinsicHeight() <= 0) getHeight() else drawable.getIntrinsicHeight()
        val bitmap: Bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas: Canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }
}
