package com.uroad.dubai.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.uroad.dubai.R
import com.uroad.dubai.activity.*
import com.uroad.dubai.common.DubaiApplication
import com.uroad.dubai.enumeration.MessageType
import com.uroad.dubai.utils.DubaiUtils

class MessagePushService : FirebaseMessagingService() {
    companion object {
//        private const val TAG = "FirebaseMsgService"
    }

    private var notificationID = 0x0000
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "From: ${remoteMessage?.from}")
//        Log.d(TAG, "messageId: ${remoteMessage?.messageId}")
//        Log.d(TAG, "messageType: ${remoteMessage?.messageType}")
//        remoteMessage?.data?.let { Log.d(TAG, "Message data payload: $it") }
//        remoteMessage?.data?.isNotEmpty()?.let {
//            Log.d(TAG, "Message data payload: " + remoteMessage.data)
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }
//        }
        remoteMessage?.let { whenMessageReceived(it) }
//        remoteMessage?.notification?.let { sendNotification(it.body) }
        // Check if message contains a notification payload.
        remoteMessage?.notification?.let {
            //            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private fun whenMessageReceived(message: RemoteMessage) {
        //判断app进程是否存活
        var pendingIntent: PendingIntent? = null
        if (DubaiUtils.isAppAlive(this, this.packageName)) {
            pendingIntent = whenAppAlive(message)
        } else {
            whenAppDeath(message)
        }
        showNotification(message, pendingIntent)
    }

    private fun whenAppAlive(message: RemoteMessage): PendingIntent {
        val mainIntent = Intent(this, MainActivity::class.java)
        //将MainAtivity的launchMode设置成SingleTask, 或者在下面flag中加上Intent.FLAG_CLEAR_TOP,
        //如果Task栈中有MainActivity的实例，就会把它移到栈顶，把在它之上的Activity都清理出栈，
        //如果Task栈不存在MainActivity实例，则在栈顶创建
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val detailIntent: Intent
        //1009001  event 1009002  news 1009003  notice 1009004  system
        when (message.data?.get("msgtype")) {
            MessageType.EVENT.CODE -> {
                detailIntent = Intent(this, EventsDetailActivity::class.java).apply { putExtras(Bundle().apply { putString("eventId", message.data?.get("id")) }) }
            }
            MessageType.NOTICE.CODE -> {
                detailIntent = Intent(this, NoticeListActivity::class.java)
            }
            MessageType.NEWS.CODE -> {
                detailIntent = Intent(this, NewsDetailsActivity::class.java).apply { putExtras(Bundle().apply { putString("newsId", message.data?.get("id")) }) }
            }
            MessageType.SYSTEM.CODE -> {
                detailIntent = Intent(this, MessagesListActivity::class.java)
            }
            else -> {
                detailIntent = Intent(this, MainActivity::class.java)
            }
        }
        val intents = arrayOf(mainIntent, detailIntent)
        return PendingIntent.getActivities(this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun whenAppDeath(message: RemoteMessage) {
        //如果app进程已经被杀死，先重新启动app，将DetailActivity的启动参数传入Intent中，参数经过
        //SplashActivity传入MainActivity，此时app的初始化已经完成，在MainActivity中就可以根据传入
        // 参数跳转到DetailActivity中去了
        val launchIntent = this.packageManager.getLaunchIntentForPackage(this.packageName)
        launchIntent?.let {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            it.putExtras(Bundle().apply {
                putString(Constants.PUSH_MESSAGE_ID, message.data?.get("id"))
                putString(Constants.PUSH_MESSAGE_TYPE, message.data?.get("msgtype"))
                putString(Constants.PUSH_MESSAGE_BODY, message.notification?.body)
            })
            startActivity(it)
        }
    }


    private fun showNotification(message: RemoteMessage, pendingIntent: PendingIntent?) {
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message.notification?.body)
                .setAutoCancel(true)
                .setShowWhen(true)
                .setTicker(message.notification?.body) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(notificationID /* ID of notification */, notificationBuilder.build())
        notificationID++
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String?) {
//        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token)
        initFirebase()
    }
    // [END on_new_token]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
        val myJob = dispatcher.newJobBuilder()
                .setService(PushJobService::class.java)
                .setTag("my-job-tag")
                .build()
        dispatcher.schedule(myJob)
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
//        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        initFirebase()
    }

    private fun initFirebase() {
        FirebaseMessaging.getInstance().subscribeToTopic("fcm_test")
        FirebaseMessaging.getInstance().subscribeToTopic("${MessageTopic.EVENT.CODE}${DubaiApplication.getUserId()}")
        FirebaseMessaging.getInstance().subscribeToTopic(MessageTopic.NEWS.CODE)
        FirebaseMessaging.getInstance().subscribeToTopic(MessageTopic.NOTICE.CODE)
        FirebaseMessaging.getInstance().subscribeToTopic(MessageTopic.SYSTEM.CODE)
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}