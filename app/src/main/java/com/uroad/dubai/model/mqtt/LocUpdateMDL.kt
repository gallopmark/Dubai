package com.uroad.dubai.model.mqtt

import com.uroad.dubai.utils.GsonUtils
import org.eclipse.paho.client.mqttv3.MqttMessage

class LocUpdateMDL {
    var teamid: String? = null
    var userid: String? = null
    var username: String? = null
    var latitude: Double? = 0.0
    var longitude: Double? = 0.0

    fun obtainMessage(): MqttMessage {
        return MqttMessage().apply {
            qos = 2
            isRetained = false
            payload = GsonUtils.fromObjectToJson(this@LocUpdateMDL).toByteArray()
        }
    }
}