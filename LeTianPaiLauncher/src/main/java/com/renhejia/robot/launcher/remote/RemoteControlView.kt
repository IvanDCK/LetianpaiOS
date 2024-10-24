package com.renhejia.robot.launcher.remote

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.renhejia.robot.launcher.R

/**
 * @author liujunbin
 */
class RemoteControlView : LinearLayout {
    private var remoteView: LinearLayout? = null
    private var imageView: ImageView? = null
    private var remoteText: TextView? = null
    private var mContext: Context? = null

    constructor(context: Context?) : super(context) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    protected fun initView(context: Context?) {
        this.mContext = context
        inflate(context, R.layout.robot_remote_control_view, this)
        remoteView = findViewById(R.id.root_remote)
        imageView = findViewById(R.id.remote_image)
        remoteText = findViewById(R.id.remote_text)
    }

    fun showRemoteChangeView() {
        //        remoteView.getBackground().setAlpha(256);
//        remoteView.getBackground().setAlpha(256);

        remoteText!!.setText(R.string.remote_start)
        //        LogUtils.logi("letianpai","long: "+ViewUtils.getViewHeightSize())
//        ViewUtils.resizeImageViewSize();
    }

    fun showRemoteImageView() {
        remoteView!!.background.alpha = 0
        imageView!!.background.alpha = 0
        remoteText!!.visibility = GONE
        imageView!!.visibility = VISIBLE
    }

    fun showRemoteTextView(text: String?) {
//        remoteView.getBackground().setAlpha(0);
        imageView!!.visibility = GONE
        remoteText!!.text = text
    }
}
