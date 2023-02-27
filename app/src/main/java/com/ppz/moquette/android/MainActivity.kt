package com.ppz.moquette.android

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.blankj.utilcode.util.NetworkUtils

class MainActivity : AppCompatActivity() {

    private val tvIp by lazy { findViewById<TextView>(R.id.tv_ip) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvIp.text = "${NetworkUtils.getIPAddress(true)}:${ServerInstance.MQTT_SERVER_PORT}"

        startService(Intent(this, MQTTServer::class.java))
    }

}