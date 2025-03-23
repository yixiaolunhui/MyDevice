package com.zwl.mydevice.lock

import android.app.AlertDialog
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.zwl.mydevice.R

class LockActivity : ComponentActivity() {
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var componentName: ComponentName
    private lateinit var deviceLockHelper: DeviceLockHelper
    private lateinit var countdownTimer: LockScreenCountdownTimer
    private var selectedDelayMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)

        deviceLockHelper = DeviceLockHelper(this, devicePolicyManager, componentName)

        val selectTimeButton: Button = findViewById(R.id.select_time_button)
        val startLockButton: Button = findViewById(R.id.start_lock_button)
        val selectedTimeTextView: TextView = findViewById(R.id.selected_time_textview)

        // 初始化倒计时类
        countdownTimer = LockScreenCountdownTimer(this, selectedTimeTextView, deviceLockHelper) {
            selectedDelayMillis = 0
        }

        // 设置倒计时选择按钮的点击事件
        selectTimeButton.setOnClickListener {
            showCountdownOptions { selectedMillis ->
                selectedDelayMillis = selectedMillis
                countdownTimer.cancel()
                selectedTimeTextView.text = "倒计时锁屏时间：${selectedMillis / 60000} 分钟"
            }
        }

        // 设置开始锁屏倒计时按钮的点击事件
        startLockButton.setOnClickListener {
            // 检查设备管理员是否启用
            if (!deviceLockHelper.isAdminActive()) {
                deviceLockHelper.activateDeviceAdmin()
            } else {
                if (selectedDelayMillis > 0) {
                    countdownTimer.startCountdown(selectedDelayMillis)  // 启动新的倒计时
                } else {
                    Toast.makeText(this, "请先选择倒计时时间", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showCountdownOptions(onTimeSelected: (Long) -> Unit) {
        val options = arrayOf("1分钟", "10分钟", "15分钟", "30分钟", "1小时", "1.5小时", "2小时")
        val timeInMillis = arrayOf(
            1 * 60 * 1000L,  // 1分钟
            10 * 60 * 1000L,  // 10分钟
            15 * 60 * 1000L,  // 15分钟
            30 * 60 * 1000L,  // 30分钟
            60 * 60 * 1000L,  // 1小时
            90 * 60 * 1000L,  // 1.5小时
            120 * 60 * 1000L  // 2小时
        )

        AlertDialog.Builder(this).setTitle("选择倒计时锁屏时间").setItems(options) { _, which ->
            onTimeSelected(timeInMillis[which])
        }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DeviceLockHelper.REQUEST_CODE_ENABLE_ADMIN && resultCode == RESULT_OK) {
            Toast.makeText(this, "设备管理员已激活", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "设备管理员激活失败", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer.cancel()
    }
}
