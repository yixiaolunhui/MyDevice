package com.zwl.mydevice.lock

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        Toast.makeText(context, "设备管理员已启用", Toast.LENGTH_SHORT).show()
        super.onEnabled(context, intent)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        Toast.makeText(context, "设备管理员已禁用", Toast.LENGTH_SHORT).show()
        super.onDisabled(context, intent)
    }
}
