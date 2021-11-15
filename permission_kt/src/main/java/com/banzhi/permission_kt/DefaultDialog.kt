package com.banzhi.permission_kt

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import com.banzhi.permission_kt.databinding.PermissionDefaultDialogLayoutBinding

/**
 *<pre>
 * @author : jiang
 * @time : 2021/11/12.
 * @desciption :
 * @version :
 *</pre>
 */
class DefaultDialog(
    context: Context,
    private var permission: String?,
    private var message: String?,
    private var block: (index: Int) -> Unit
) : Dialog(context, R.style.DefaultDialog) {
    private lateinit var binding: PermissionDefaultDialogLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PermissionDefaultDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
        setupText()
        setupWindow()

    }

    private fun setListener() {
        binding.positiveBtn.setOnClickListener {
            block.invoke(0)
            dismiss()
        }
        binding.negativeBtn.setOnClickListener {
            block.invoke(1)
            dismiss()
        }

    }

    private fun setupText() {
        binding.messageText.text = "${message}需要您同意以下权限再能正常使用"
        when (permission) {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                binding.messagePermission.text =
                    context.getString(R.string.permission_access_background_location)
            }
            Manifest.permission.SYSTEM_ALERT_WINDOW -> {
                binding.messagePermission.text =
                    context.getString(R.string.permission_system_alert_window)
            }
            Manifest.permission.WRITE_SETTINGS -> {
                binding.messagePermission.text =
                    context.getString(R.string.permission_write_settings)
            }
            Manifest.permission.MANAGE_EXTERNAL_STORAGE -> {
                binding.messagePermission.text =
                    context.getString(R.string.permission_manage_external_storage)
            }
            Manifest.permission.REQUEST_INSTALL_PACKAGES -> {
                binding.messagePermission.text =
                    context.getString(R.string.permission_request_install_packages)
            }
        }
    }

    private fun setupWindow() {
        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        if (width < height) {
            // now we are in portrait
            window?.let {
                val param = it.attributes
                it.setGravity(Gravity.CENTER)
                param.width = (width * 0.86).toInt()
                it.attributes = param
            }
        } else {
            // now we are in landscape
            window?.let {
                val param = it.attributes
                it.setGravity(Gravity.CENTER)
                param.width = (width * 0.6).toInt()
                it.attributes = param
            }
        }
    }
}