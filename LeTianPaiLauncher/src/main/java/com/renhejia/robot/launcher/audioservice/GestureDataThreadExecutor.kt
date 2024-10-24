package com.renhejia.robot.launcher.audioservice

import com.renhejia.robot.commandlib.log.LogUtils.logd
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future


class GestureDataThreadExecutor private constructor() : Executor {
    var currentTask: Future<*>? = null
    private val mExecutorService: ExecutorService =
        Executors.newSingleThreadExecutor()

    override fun execute(task: Runnable) {
        if (currentTask != null && !currentTask!!.isDone) {
            logd("GestureDataThreadExecutor", "execute: cancel thread")
            currentTask!!.cancel(true)
        } else {
            logd("GestureDataThreadExecutor", "execute: thread empty")
        }
        currentTask = mExecutorService.submit(task)
    }

    companion object {
        private var gestureDataThreadExecutor: GestureDataThreadExecutor? = null

        val instance: GestureDataThreadExecutor
            get() {
                if (gestureDataThreadExecutor == null) {
                    gestureDataThreadExecutor =
                        GestureDataThreadExecutor()
                }
                return gestureDataThreadExecutor!!
            }
    }
}
