package com.renhejia.robot.launcherbusinesslib.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Launcher main activity基类
 *
 * @author liujunbin
 */
abstract class LauncherBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setContentView(R.layout.activity_launcher_base);
        init(this)
    }

    /**
     * @param context
     */
    private fun init(context: Context) {
        //TODO 初始化数据，view ..
    }
}