package com.application.stations.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import com.application.stations.core.BaseActivity
import com.application.stations.databinding.ActivitySplashScreenBinding
import com.application.stations.utils.extension.backgroundColorStatusBar
import com.application.stations.utils.extension.delay
import com.application.stations.utils.extension.goto

@SuppressLint("CustomSplashScreen")
class SplashScreen : BaseActivity<ActivitySplashScreenBinding>() {

    override val binding by lazy { ActivitySplashScreenBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backgroundColorStatusBar()

        start()
    }

    private fun start(){
        delay(2000){
            goto(Home(), true)
        }
    }
}