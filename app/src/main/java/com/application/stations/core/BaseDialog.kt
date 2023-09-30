package com.application.stations.core

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class BaseDialog<VB : ViewBinding> : DialogFragment(){

    protected abstract val binding: VB

    abstract fun onConfigured()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val wlp: WindowManager.LayoutParams = window!!.attributes.apply {
                gravity = Gravity.CENTER
            }
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            window?.attributes = wlp
        }

        onConfigured()

        return binding.root
    }

    fun onForcedDialog(isForced: Boolean){
        dialog?.apply {
            setCancelable(!isForced)
            setCanceledOnTouchOutside(!isForced)
        }
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}