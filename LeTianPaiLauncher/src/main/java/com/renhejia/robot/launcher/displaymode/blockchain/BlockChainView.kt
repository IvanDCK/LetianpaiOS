package com.renhejia.robot.launcher.displaymode.blockchain

import android.content.Context
import android.util.AttributeSet
import com.renhejia.robot.launcher.displaymode.base.DisplayModeBaseView

/**
 * 区块链view
 *
 * @author liujunbin
 */
class BlockChainView : DisplayModeBaseView {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?,  attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)
}
