package com.ppz.moquette.android

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.blankj.utilcode.util.LogUtils
import com.ppz.moquette.android.ServerInstance.service
import io.moquette.BrokerConstants
import io.moquette.broker.Server
import java.io.IOException
import java.lang.Exception
import java.net.BindException
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask


object ServerInstance {

    const val MQTT_SERVER_PORT = 1883

    val service by lazy { Server() }
}

/**
 * MQTT服务端
 */
class MQTTServer : Service() {

    private val TAG = MQTTServer::class.java.simpleName
    private var thread: Thread? = null
    private var mqttBroker: MQTTBroker? = null

    private val properties by lazy {
        Properties().apply {
            setProperty(BrokerConstants.PORT_PROPERTY_NAME, "${ServerInstance.MQTT_SERVER_PORT}")
            set(BrokerConstants.NETTY_EPOLL_PROPERTY_NAME, true)
            //很tmd重要。https://github.com/moquette-io/moquette/issues/535
            set(BrokerConstants.IMMEDIATE_BUFFER_FLUSH_PROPERTY_NAME, true)
            setProperty(BrokerConstants.IMMEDIATE_BUFFER_FLUSH_PROPERTY_NAME, true.toString())
            set(BrokerConstants.NETTY_MAX_BYTES_PROPERTY_NAME, 8092)
            set(BrokerConstants.INFLIGHT_WINDOW_SIZE, 20)
            setProperty(BrokerConstants.WEB_SOCKET_PORT_PROPERTY_NAME, BrokerConstants.WEBSOCKET_PORT.toString())
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startBroker()
        return START_STICKY
    }

    private fun startBroker(): Int {
        try {
            mqttBroker = MQTTBroker(properties)
            val futureTask = FutureTask(mqttBroker)
            if (thread == null || thread?.isAlive == true) {
                thread = Thread(futureTask)
                thread?.name = "MQTT_Server"
                thread?.start()
                if (futureTask.get()) {
                    LogUtils.i(TAG, "本地MQTT启动")
                } else {
                    LogUtils.e(TAG, "本地MQTT启动失败")
                }
            }
            LogUtils.i("启动MQTTServer")
        } catch (e: ExecutionException) {
            e.printStackTrace()
            LogUtils.e(TAG, "本地MQTT启动失败")
            stopSelf()
        } catch (e: Exception) {
            e.printStackTrace()
            return START_NOT_STICKY
        }
        return START_STICKY
    }


    override fun onDestroy() {
        if (thread != null) {
            mqttBroker?.stopServer()
            thread?.interrupt()
            LogUtils.e(TAG, "本地MQTT停止")
        }
        super.onDestroy()
    }


}


class MQTTBroker(private val config: Properties) : Callable<Boolean> {

    fun stopServer() {
        try {
            service.stopServer()
        } catch (e: Exception) {
            e.message
        }
    }

    override fun call(): Boolean {
        try {
            service.startServer(config)
            return true
        } catch (e: BindException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }
}