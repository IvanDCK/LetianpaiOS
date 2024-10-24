package com.letianpai.robot.audioservice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.letianpai.robot.audioservice.service.LTPAudioService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(
            this@MainActivity,
            LTPAudioService::class.java
        )
        startService(intent)
    }
}