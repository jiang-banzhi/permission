package com.banzhi.permission_kt

import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.banzhi.permission_kt.databinding.PurposeOfUseViewBinding


/**
 *<pre>
 * @author : jiang
 * @time : 2023/12/15.
 * @desciption :
 * @version :
 *</pre>
 */
class PermissionPurposeOfUseTipView(var context: Context, var purpose: String) :
    IPurposeView {
    private var purposeView: AlertDialog
    private var binding: PurposeOfUseViewBinding

    init {
        binding = PurposeOfUseViewBinding.inflate(LayoutInflater.from(context))
        binding.tvPurpose.text = purpose
        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)
        purposeView = builder.create()
        val window = purposeView.getWindow()
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.gravity = Gravity.TOP
            window.setAttributes(layoutParams)
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }


    }


    override fun show() {
        purposeView.show()
    }

    override fun dismiss() {
        purposeView.dismiss()
    }
}