package com.application.stations.utils.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.application.stations.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun uiThread(fn: () -> Unit){
    Handler(Looper.getMainLooper()).post {
        fn()
    }
}

fun delay(delay: Long, fn: () -> Unit){
    Handler(Looper.getMainLooper()).postDelayed({
        fn()
    }, delay)
}

fun Context.goto(activity: Activity, closeAll: Boolean= false){
    val intent= Intent(this, activity::class.java)
    if(closeAll) intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
    unwrap().overridePendingTransition(R.anim.activity_fade_in, R.anim.none)
}