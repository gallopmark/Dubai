package com.uroad.mqtt

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken

class SimpleMqttCallback :IMqttCallback{
    override fun messageArrived(topic: String, message: String, qos: Int) {
    }

    override fun connectionLost(cause: Throwable) {
    }

    override fun deliveryComplete(token: IMqttDeliveryToken) {
    }

    override fun connectSuccess(asyncActionToken: IMqttToken?) {
    }

    override fun connectFailed(asyncActionToken: IMqttToken?, exception: Throwable?) {
    }
}