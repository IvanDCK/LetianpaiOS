package com.renhejia.robot.launcherbusinesslib.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

abstract class DesktopView : RelativeLayout {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //    public abstract void playUnReadMessageAnimator();
    //
    //    public abstract void stopUnReadMessageAnimator();
    abstract fun unregisterActivationReceiver()
}
