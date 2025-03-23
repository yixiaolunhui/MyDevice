package com.zwl.mydevice.lock

import android.content.Context
import android.os.CountDownTimer
import android.widget.TextView
import android.widget.Toast
import com.zwl.mydevice.R
import kotlin.math.ceil

class LockScreenCountdownTimer(
    private val context: Context,
    private val textView: TextView,
    private val deviceLockHelper: DeviceLockHelper,
    private val countdownFinish:()->Unit,
) {

    private var countdownTimer: CountDownTimer? = null

    fun startCountdown(delayMillis: Long) {
        cancel()
        countdownTimer = object : CountDownTimer(delayMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val formattedTime = formatTime(millisUntilFinished)
                textView.text = "锁屏倒计时：$formattedTime"
            }

            override fun onFinish() {
                deviceLockHelper.lockScreen()
                textView.text = context.getString(R.string.select_lock_time)
                countdownFinish.invoke()
            }
        }.start()

        val delayInMinutes = ceil(delayMillis / 60000.0).toInt()
        Toast.makeText(context, "设备将在 $delayInMinutes 分钟后锁屏", Toast.LENGTH_SHORT).show()
    }

    // 格式化剩余时间为 xx小时xx分钟xx秒
    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return buildString {
            if (hours > 0) append("${hours}小时")
            if (minutes > 0 || hours > 0) append("${minutes}分钟")
            append("${seconds}秒")
        }
    }

    // 取消当前倒计时
    fun cancel() {
        countdownTimer?.cancel()
        countdownTimer = null
    }
}
