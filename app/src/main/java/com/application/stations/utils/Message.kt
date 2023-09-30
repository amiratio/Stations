package com.application.stations.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.application.stations.R
import com.application.stations.ui.dialog.AlertDialog
import com.application.stations.utils.extension.uiThread
import com.application.stations.utils.extension.unwrap

class Message(private val context: Context) {

    private fun custom(title: String, description: String, button: String, gr: GR<Boolean>?= null, isForced: Boolean= false) {
        uiThread {
            val dialog= AlertDialog(title, description, button, gr, isForced)
            dialog.show((context.unwrap() as AppCompatActivity).supportFragmentManager, "AlertDialog")
        }
    }

    fun gpsRequired(gr: GR<Boolean>) = context.apply {
        custom(
            getString(R.string.location_permission_required_title),
            getString(R.string.location_permission_required_description),
            getString(R.string.location_permission_required_button),
            gr,
            isForced = true
        )
    }

    fun noInternet() = context.apply {
        custom(
            getString(R.string.no_internet_title),
            getString(R.string.no_internet_description),
            getString(R.string.ok),
        )
    }

    fun bookFailed() = context.apply {
        custom(
            getString(R.string.book_failed_title),
            getString(R.string.book_failed_desc),
            getString(R.string.book_failed_button),
        )
    }

    fun alreadyBooked() = context.apply {
        custom(
            getString(R.string.booked_title),
            getString(R.string.booked_desc),
            getString(R.string.booked_button),
        )
    }

}