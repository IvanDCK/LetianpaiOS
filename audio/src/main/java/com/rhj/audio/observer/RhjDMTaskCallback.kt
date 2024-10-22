package com.rhj.audio.observer

import com.rhj.audio.DmTaskResultBean

interface RhjDMTaskCallback {
    fun dealResult(dmTaskResultBean: DmTaskResultBean?): String?

    fun dealErrorResult()
}
