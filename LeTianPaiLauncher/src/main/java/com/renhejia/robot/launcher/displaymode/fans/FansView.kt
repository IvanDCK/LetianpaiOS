package com.renhejia.robot.launcher.displaymode.fans

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.renhejia.robot.commandlib.parser.displaymodes.fans.FansInfo
import com.renhejia.robot.guidelib.wifi.WIFIConnectionManager.Companion.getInstance
import com.renhejia.robot.launcher.R
import com.renhejia.robot.launcher.displaymode.callback.FansInfoCallback
import com.renhejia.robot.launcher.displaymode.callback.FansInfoCallback.FansInfoUpdateListener
import com.renhejia.robot.launcher.displaymode.utils.GlideCircleTransform
import com.renhejia.robot.launcherbaselib.storage.manager.LauncherConfigManager
import com.renhejia.robot.launcherbaselib.storage.manager.LauncherConfigManager.Companion.getInstance
import java.lang.ref.WeakReference
import java.text.DecimalFormat

/**
 * @author liujunbin
 */
class FansView : RelativeLayout {
    private var mContext: Context? = null
    private var ivIcon: ImageView? = null
    private var tvName: TextView? = null
    private var tvFansNum: TextView? = null
    private var ivFans: ImageView? = null
    private var mHandler: UpdateViewHandler? = null
    private var gson: Gson? = null
    private var mFansInfo: FansInfo? = null

    constructor(context: Context) : super(context) {
        init(context)
        fillData()
        addDataUpdateListeners()
    }

    private fun fillData() {
        val fansInfo = LauncherConfigManager.getInstance(mContext!!)!!.robotFansInfo
        val fansInfos = gson!!.fromJson(fansInfo, FansInfo::class.java)
        updateViews(fansInfos)
    }

    private fun init(context: Context) {
        this.mContext = context
        gson = Gson()
        mHandler = UpdateViewHandler(mContext!!)
        inflate(context, R.layout.robot_display_fans, this)
        ivIcon = findViewById(R.id.iv_icon)
        tvFansNum = findViewById(R.id.tv_fans_num)
        tvName = findViewById(R.id.tv_name)
        ivFans = findViewById(R.id.iv_fans)
    }

    private fun addDataUpdateListeners() {
        FansInfoCallback.instance
            .setFansInfoUpdateListener { fansInfo ->
                updateFansInfo(fansInfo)
                update()

                // FansInfo{code=0, data=[FansData{platform='bilibili', avatar='https://i0.hdslb.com/bfs/face/member/noface.jpg', nick_name='圆梦', fans_count='300029'}, FansData{platform='weibo', avatar='https://i0.hdslb.com/bfs/face/member/noface.jpg', nick_name='圆梦', fans_count='2223'}], msg='success'}
                // fansInfo.getData()[0].getFans_count(): 300029
                // fansInfo.getData()[0].getAvatar(): https://i0.hdslb.com/bfs/face/member/noface.jpg
                // fansInfo.getData()[0].getNick_name(): 圆梦
            }
    }

    private fun updateViews(fansInfo: FansInfo?) {
        if (fansInfo?.data == null || fansInfo.data!!.size == 0 || fansInfo.data!![0] == null || fansInfo.data!![0].fans_count == null || fansInfo.data!![0].nick_name == null || fansInfo.data!![0].avatar == null
        ) {
            return
        }
        Log.e("letianpai_20230427", "fansInfo:$fansInfo")
        val number = fansInfo.data!![0].fans_count!!.toInt()
        if (number > 10000) {
            val numbers = number.toFloat() / 10000
            val df = DecimalFormat("0.0") //格式化小数
            val s = df.format(numbers.toDouble())
            tvFansNum!!.text = s + "w"
        } else {
            tvFansNum!!.setText(number)
        }
        if (fansInfo.data!![0].platform == BILIBILI) {
            ivFans!!.setImageResource(R.drawable.bili_fans)
        } else {
            ivFans!!.setImageResource(R.drawable.weibo_fans)
        }

        tvName!!.text = fansInfo.data!![0].nick_name
        val url = fansInfo.data!![0].avatar
        uploadIcon(url)
    }

    private fun updateFansInfo(fansInfo: FansInfo) {
        this.mFansInfo = fansInfo
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private fun uploadIcon(url: String?) {
        val message = Message()
        message.what = UPDATE_ICON
        message.obj = url
        mHandler!!.sendMessage(message)
    }

    private fun update() {
        val message = Message()
        message.what = UPDATE_STATUS
        mHandler!!.sendMessage(message)
    }

    private inner class UpdateViewHandler(context: Context) : Handler(Looper.getMainLooper()) {
        private val contextRef = WeakReference(context)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val context = contextRef.get()
            if (context != null) {
                when (msg.what) {
                    UPDATE_ICON -> {
                        Glide.with(context)
                            .load(msg.obj as String)
                            .centerCrop()
                            .transform(GlideCircleTransform())
                            .into(ivIcon!!)
                    }
                    UPDATE_STATUS -> {
                        updateViews(mFansInfo)
                    }
                }
            }
        }
    }


    companion object {
        private const val UPDATE_ICON = 110
        private const val UPDATE_STATUS = 111
        private const val BILIBILI = "bilibili"
    }
}
