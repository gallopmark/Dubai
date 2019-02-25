package com.uroad.mqtt

import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken

interface IMqttCallback {
    /**
     * 收到消息
     *
     * @param topic   主题
     * @param message 消息内容
     * @param qos     消息策略
     */
    fun messageArrived(topic: String, message: String, qos: Int)

    /**
     * 连接断开
     *
     * @param arg0 抛出的异常信息
     */
    fun connectionLost(cause: Throwable)

    /**
     * 传送完成
     *
     * @param token
     */
    fun deliveryComplete(token: IMqttDeliveryToken)

    /**
     * 连接成功
     *
     * @param asyncActionToken
     */
    fun connectSuccess(asyncActionToken: IMqttToken?)

    /**
     * 连接失败
     *
     * @param asyncActionToken
     */
    fun connectFailed(asyncActionToken: IMqttToken?, exception: Throwable?)
}