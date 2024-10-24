package com.renhejia.robot.launcher.displaymode

import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.renhejia.robot.launcher.R


class DisplayModesView : RelativeLayout {
    private var mContext: android.content.Context? = null
    private var background: android.widget.ImageView? = null

    constructor(context: android.content.Context) : super(context) {
        init(context)
    }

    private fun init(context: android.content.Context) {
        this.mContext = context
        android.view.View.inflate(context, R.layout.robot_display, this)
        background = findViewById(R.id.iv_background)
    }

    fun showViews() {
        android.util.Log.i("DisplayModesView", "showViews")
        mContext?.let {
            background?.let { imageView ->
                Glide.with(it)
                    .asGif()
                    .load(com.renhejia.robot.expression.R.drawable.default_start)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(imageView)
            }
        }
    }

    fun hideViews() {
        android.util.Log.i("DisplayModesView", "hideViews")
        background!!.setImageDrawable(null)
    }

    constructor(
        context: android.content.Context,
        attrs: android.util.AttributeSet?
    ) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: android.content.Context,
        attrs: android.util.AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }
}
