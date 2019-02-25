package com.uroad.mqtt

import android.content.Context
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.IMqttMessageListener


class MQTTService(builder: Builder) {

    private lateinit var client: MqttAndroidClient
    private lateinit var conOpt: MqttConnectOptions

    private var context: Context? = null
    private var serverUrl: String? = ""
    private var userName = "admin"
    private var passWord = "password"
    private var clientId: String = ""
    private var timeOut = 10
    private var keepAliveInterval = 20
    private var retained = false
    private var cleanSession = false
    private var autoReconnect = true
    private var starMQTTCallBack: IMqttCallback? = null

    init {
        this.context = builder.context
        this.serverUrl = builder.serverUrl
        this.userName = builder.userName
        this.passWord = builder.passWord
        this.clientId = builder.clientId
        this.timeOut = builder.timeOut
        this.keepAliveInterval = builder.keepAliveInterval
        this.retained = builder.retained
        this.cleanSession = builder.cleanSession
        this.autoReconnect = builder.autoReconnect
        initMqtt()
    }

    private fun initMqtt() {
        // 服务器地址（协议+地址+端口号）
        client = MqttAndroidClient(context, serverUrl, clientId).apply { setCallback(iMQTTCallback) }
        // 设置MQTT监听并且接受消息
        conOpt = MqttConnectOptions().apply {
            isCleanSession = this@MQTTService.cleanSession  // 清除缓存
            connectionTimeout = this@MQTTService.timeOut // 设置超时时间，单位：秒
            keepAliveInterval = this@MQTTService.keepAliveInterval // 心跳包发送间隔，单位：秒
            userName = this@MQTTService.userName // 用户名
            password = this@MQTTService.passWord.toCharArray() // 密码
            isAutomaticReconnect = this@MQTTService.autoReconnect
            serverURIs = arrayOf(serverUrl)
        }
    }

    fun connect(starMQTTCallBack: IMqttCallback?) {
        this.starMQTTCallBack = starMQTTCallBack
        if (!client.isConnected) {
            try {
                client.connect(conOpt, null, iMqttActionListener)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun subscribe(topics: String, qos: Int) {
        if (!isConnected()) return
        try {
            // 订阅topic话题
            client.subscribe(topics, qos)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 订阅主题
     *
     * @param topics 主题
     * @param qos    策略
     */
    fun subscribe(topics: Array<String>, qos: IntArray) {
        if (!isConnected()) return
        try {
            // 订阅topic话题
            client.subscribe(topics, qos)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun subscribe(topicFilter: String, qos: Int, userContext: Any, callback: IMqttActionListener) {
        if (!isConnected()) return
        try {
            // 订阅topic话题
            client.subscribe(topicFilter, qos, userContext, callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun subscribe(topicFilters: Array<String>, qos: IntArray, userContext: Any, callback: IMqttActionListener) {
        if (!isConnected()) return
        try {
            // 订阅topic话题
            client.subscribe(topicFilters, qos, userContext, callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun subscribe(topicFilter: String, qos: Int, userContext: Any, callback: IMqttActionListener, messageListener: IMqttMessageListener) {
        if (!isConnected()) return
        try {
            // 订阅topic话题
            client.subscribe(topicFilter, qos, userContext, callback, messageListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun subscribe(topicFilter: String, qos: Int, messageListener: IMqttMessageListener) {
        if (!isConnected()) return
        try {
            // 订阅topic话题
            client.subscribe(topicFilter, qos, messageListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun subscribe(topicFilters: Array<String>, qos: IntArray, messageListeners: Array<IMqttMessageListener>) {
        if (!isConnected()) return
        try {
            // 订阅topic话题
            client.subscribe(topicFilters, qos, messageListeners)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun subscribe(topicFilters: Array<String>, qos: IntArray, userContext: Any, callback: IMqttActionListener, messageListeners: Array<IMqttMessageListener>) {
        if (!isConnected()) return
        try {
            // 订阅topic话题
            client.subscribe(topicFilters, qos, userContext, callback, messageListeners)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun publish(topic: String, payload: ByteArray, qos: Int,
                retained: Boolean) {
        if (!isConnected()) return
        try {
            client.publish(topic, payload, qos, retained)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String, message: MqttMessage) {
        if (!isConnected()) return
        try {
            client.publish(topic, message)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun publish(topic: String, payload: ByteArray, qos: Int,
                retained: Boolean, userContext: Any, callback: IMqttActionListener) {
        if (!isConnected()) return
        try {
            client.publish(topic, payload, qos, retained, userContext, callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String, message: MqttMessage,
                userContext: Any, callback: IMqttActionListener) {
        if (!isConnected()) return
        try {
            client.publish(topic, message, userContext, callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun unsubscribe(topicFilter: String) {
        if (!isConnected()) return
        try {
            client.unsubscribe(topicFilter)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun unsubscribe(topic: Array<String>) {
        if (!isConnected()) return
        try {
            client.unsubscribe(topic)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unsubscribe(topicFilter: String, userContext: Any, callback: IMqttActionListener) {
        if (!isConnected()) return
        try {
            client.unsubscribe(topicFilter, userContext, callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun unsubscribe(topicFilters: Array<String>, userContext: Any, callback: IMqttActionListener) {
        if (!isConnected()) return
        try {
            client.unsubscribe(topicFilters, userContext, callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 断开连接
     */
    fun disconnect() {
        try {
            client.unregisterResources()
            client.close()
            client.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 判断连接是否断开
     */
    fun isConnected(): Boolean {
        return try {
            client.isConnected
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // MQTT监听并且接受消息
    private val iMQTTCallback = object : MqttCallback {

        @Throws(Exception::class)
        override fun messageArrived(topic: String, message: MqttMessage) {
            val msgContent = String(message.payload)
            starMQTTCallBack?.messageArrived(topic, msgContent, message.qos)
        }

        override fun deliveryComplete(token: IMqttDeliveryToken) {
            starMQTTCallBack?.deliveryComplete(token)
        }

        // 失去连接，重连
        override fun connectionLost(cause: Throwable) {
            starMQTTCallBack?.connectionLost(cause)
        }
    }

    private val iMqttActionListener = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            starMQTTCallBack?.connectSuccess(asyncActionToken)
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            starMQTTCallBack?.connectFailed(asyncActionToken, exception)
        }
    }

    /**
     * 关闭客户端
     */
    fun close() {
        try {
            client.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Builder 构造类
     */
    class Builder {
        internal var context: Context? = null
        internal var serverUrl: String? = null
        internal var userName = "admin"
        internal var passWord = "password"
        internal var clientId: String = ""
        internal var timeOut = 10
        internal var keepAliveInterval = 20
        internal var retained = false
        internal var cleanSession = false
        internal var autoReconnect = false

        fun serverUrl(serverUrl: String): Builder {
            this.serverUrl = serverUrl
            return this
        }

        fun userName(userName: String): Builder {
            this.userName = userName
            return this
        }

        fun passWord(passWord: String): Builder {
            this.passWord = passWord
            return this
        }

        fun clientId(clientId: String): Builder {
            this.clientId = clientId
            return this
        }

        fun timeOut(timeOut: Int): Builder {
            this.timeOut = timeOut
            return this
        }

        fun keepAliveInterval(keepAliveInterval: Int): Builder {
            this.keepAliveInterval = keepAliveInterval
            return this
        }

        fun retained(retained: Boolean): Builder {
            this.retained = retained
            return this
        }

        fun autoReconnect(autoReconnect: Boolean): Builder {
            this.autoReconnect = autoReconnect
            return this
        }

        fun cleanSession(cleanSession: Boolean): Builder {
            this.cleanSession = cleanSession
            return this
        }

        fun bulid(context: Context): MQTTService {
            this.context = context
            return MQTTService(this)
        }
    }
}