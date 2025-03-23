package com.zwl.mydevice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.zwl.mydevice.hongguo.HongGuoActivity
import com.zwl.mydevice.lock.LockActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        findViewById<Button>(R.id.lock_time).setOnClickListener {
            startActivity(Intent(this@MainActivity, LockActivity::class.java))
        }

        findViewById<Button>(R.id.hg_jump_ad).setOnClickListener {
            startActivity(Intent(this@MainActivity, HongGuoActivity::class.java))
        }
    }

}
