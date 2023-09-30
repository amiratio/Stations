package com.application.stations.utils.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.application.stations.R


fun Activity.backgroundColorStatusBar() {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = ContextCompat.getColor(this, R.color.background)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun Activity.transparentStatusBar() {
    window.statusBarColor = getColor(android.R.color.transparent)
    window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    window.navigationBarColor = getColor(android.R.color.transparent)
    window.decorView.systemUiVisibility =
        window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
}

fun displayWidth() = Resources.getSystem().displayMetrics.widthPixels

fun displayHeight() = Resources.getSystem().displayMetrics.heightPixels

fun Context.unwrap(): Activity {
    var context: Context? = this
    while (context !is Activity && context is ContextWrapper) {
        context = context.baseContext
    }
    return context as Activity
}

fun View.show() = apply { visibility= View.VISIBLE }

fun View.hide() = apply { visibility= View.GONE }