package com.application.stations.ui.dialog

import android.content.DialogInterface
import com.application.stations.core.BaseDialog
import com.application.stations.databinding.DialogAlertBinding
import com.application.stations.utils.GR

class AlertDialog(
    private val titleText: String,
    private val descriptionText: String,
    private val buttonText: String,
    private val gr: GR<Boolean>?= null,
    private val isForced: Boolean
) : BaseDialog<DialogAlertBinding>() {

    override val binding by lazy { DialogAlertBinding.inflate(layoutInflater) }

    override fun onConfigured() {
        onForcedDialog(isForced)

        with(binding){
            title.text= titleText
            description.text= descriptionText
            button.text= buttonText

            button.setOnClickListener {
                gr?.result(true)
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        gr?.result(false)
    }

}