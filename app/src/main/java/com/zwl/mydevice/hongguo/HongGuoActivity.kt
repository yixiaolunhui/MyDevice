package com.zwl.mydevice.hongguo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.ComponentActivity
import com.zwl.mydevice.R

/**
 *@describe 红果
 *@author zwl
 *@date on 2025/3/2
 */
class HongGuoActivity:  ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hongguo)

        findViewById<View>(R.id.jump_ad).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("辅助功能->已安装的应用程序->红果短剧跳过广告->打开开关(也可以设置快捷打开方式)")
                .setPositiveButton("去设置") { dialog, _ ->
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    startActivity(intent)
                    dialog.dismiss()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

    }

}