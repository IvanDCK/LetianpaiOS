package com.renhejia.robot.expression.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.renhejia.robot.commandlib.consts.RobotExpressionConsts
import com.renhejia.robot.expression.R

/**
 * 机器人表情
 * @author liujunbin
 */
class RobotExpressionViewGif : RelativeLayout, RobotExpressionConsts {
    private var mContext: Context? = null
    private var ivExpression: ImageView? = null //
    private var tvExpression: TextView? = null //
    private val animationNull: Animation? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context
        inflate(
            mContext, R.layout.robot_expression_view,
            this
        )
        initView()
    }

    /***
     * 更新
     * @param url
     */
    fun updateView(url: String) {
        if (url.lastIndexOf(".gif") > 0) {
            mContext?.let { context ->
                ivExpression?.let { imageView ->
                    Glide.with(context)
                        .asGif()
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView)
                }
            }
        } else {
            mContext?.let { context ->
                ivExpression?.let { imageView ->
                    Glide.with(context)
                        .asBitmap()
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView)
                }
            }
        }
    }


    private fun initView() {
        ivExpression = findViewById(R.id.iv_expression)
        tvExpression = findViewById(R.id.tv_expression)

        mContext?.let {
            Glide.with(it).asBitmap()
                .load(R.drawable.launche_bg)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivExpression!!)
        }


        //        if (LocaleUtils.isChinese()){
//            Glide.with(mContext)
//                    .load(R.drawable.bluehole0415)
//                    .asGif()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(ivExpression);
//        }else{
//            Glide.with(mContext)
//                    .load(R.drawable.bluehole_en)
//                    .asGif()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(ivExpression);
//        }


//        Glide.with(mContext)
////                .load(R.drawable.blue_hole)
//                .load(R.drawable.wifi2)
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .listener(new RequestListener<Integer, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception arg0, Integer arg1,
//                                               Target<GlideDrawable> arg2, boolean arg3) {
//                        return false;
//                    }
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource,
//                                                   Integer model, Target<GlideDrawable> target,
//                                                   boolean isFromMemoryCache, boolean isFirstResource) {
//                        // 计算动画时长
////                        GifDrawable drawable = (GifDrawable) resource;
////                        GifDecoder decoder = drawable.getDecoder();
////                        for (int i = 0; i < drawable.getFrameCount(); i++) {
////                            duration += decoder.getDelay(i);
////                        }
////                        //发送延时消息，通知动画结束
////                        handler.sendEmptyMessageDelayed(MESSAGE_SUCCESS,
////                                duration);
//                        return false;
//                    }
//                }) //仅仅加载一次gif动画
//                .into(new GlideDrawableImageViewTarget(ivExpression, -1));
    }
}
