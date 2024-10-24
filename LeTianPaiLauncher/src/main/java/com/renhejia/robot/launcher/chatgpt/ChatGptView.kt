package com.renhejia.robot.launcher.chatgpt

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.renhejia.robot.launcher.R
import org.jetbrains.annotations.Nullable

/**
 * GPTView
 *
 * @author liujunbin
 */
class ChatGptView : LinearLayout {
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
        inflate(context, R.layout.robot_chat_gpt_view, this)
    }
}
