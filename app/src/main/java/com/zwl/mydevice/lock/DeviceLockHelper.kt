package com.zwl.mydevice.lock

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.widget.Toast

class DeviceLockHelper(private val context: Activity, private val devicePolicyManager: DevicePolicyManager, private val componentName: ComponentName) {

    companion object {
        const val REQUEST_CODE_ENABLE_ADMIN = 1
    }

    fun isAdminActive(): Boolean {
        return devicePolicyManager.isAdminActive(componentName)
    }

    private fun showDeviceAdminActivationRequest(): Intent {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "需要激活设备管理员权限来锁屏")
        return intent
    }

    fun lockScreen() {
        if (isAdminActive()) {
            devicePolicyManager.lockNow()
        } else {
            Toast.makeText(context, "设备管理员未激活", Toast.LENGTH_SHORT).show()
        }
    }


    fun activateDeviceAdmin() {
        if (!isAdminActive()) {
            val intent = showDeviceAdminActivationRequest()
            context.startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN)
        }
    }
}
