package com.uroad.dubai.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadSampleListener
import com.liulishuo.filedownloader.FileDownloader
import com.uroad.dubai.R
import com.uroad.dubai.utils.AppUtils
import java.io.File
import java.math.RoundingMode
import java.text.NumberFormat

/**
 * @author MFB
 * @create 2019/1/26
 * @describe
 */
class VersionUpdateService : Service() {
    companion object {
        private const val NOTIFICATION_ID = 0x0001
        private const val PRIMARY_CHANNEL = "default"
        private const val ACTION_CLICK = "ACTION_CLICK"
    }

    private lateinit var notificationManager: NotificationManager
    private var mBuilder: NotificationCompat.Builder? = null
    private var url: String? = null
    private var isForce: Boolean = false
    private var isPause: Boolean = false
    private var isError: Boolean = false
    override fun onBind(intent: Intent): IBinder? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.action?.let {
                if (it == ACTION_CLICK) {
                    if (isError) {
                        execute()
                    } else {
                        if (!isForce) {  //不是强制更新  可以点击暂停下载
                            if (isPause) {
                                execute()
                            } else {
                                FileDownloader.getImpl().pause(downloadListener)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        FileDownloader.setup(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        registerReceiver(receiver, IntentFilter(ACTION_CLICK))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopSelf()
        } else {
            url = intent.getStringExtra("downloadUrl")
            isForce = intent.getBooleanExtra("isForce", false)
            val intentClick = Intent(ACTION_CLICK)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intentClick, PendingIntent.FLAG_CANCEL_CURRENT)
            mBuilder = NotificationCompat.Builder(this, PRIMARY_CHANNEL).apply {
                this.setSmallIcon(R.mipmap.ic_logo)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.version_waiting_update))
                        .setChannelId(NOTIFICATION_ID.toString()) //该句适配android 8.0 版本
                        .setDefaults(Notification.DEFAULT_LIGHTS or
                                Notification.FLAG_INSISTENT or
                                Notification.FLAG_NO_CLEAR)
                        .setProgress(100, 0, true)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setContentIntent(pendingIntent)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(NOTIFICATION_ID.toString(), VersionUpdateService::class.java.name, NotificationManager.IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(channel)
            }
            execute()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun execute() {
        isPause = false
        isError = false
        FileDownloader.getImpl().create(url)
                .setForceReDownload(isForce)
                .setPath(getSavePath(), false)
                .setListener(downloadListener)
                .start()
    }

    private fun getSavePath(): String {
        val fileName = AppUtils.getFileName(url)
        return AppUtils.getApkPath(this.applicationContext) + File.separator + fileName
    }

    private val downloadListener = object : FileDownloadSampleListener() {

        override fun connected(task: BaseDownloadTask?, etag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {
            notifyId()
        }

        override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
            mBuilder?.setProgress(totalBytes, soFarBytes, true)
            notifyId()
        }

        override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
            val text = String.format(getString(R.string.version_update_notification_content),
                    AppUtils.getReadableFileSize(totalBytes.toLong()),
                    accuracy(soFarBytes.toDouble(), totalBytes.toDouble()))
            mBuilder?.setProgress(totalBytes, soFarBytes, false)
            mBuilder?.setContentText(text)
            notifyId()
        }

        private fun accuracy(num: Double, total: Double): String {
            val df = NumberFormat.getInstance().apply {
                maximumFractionDigits = 0 //可以设置精确几位小数
                roundingMode = RoundingMode.HALF_UP  //模式 例如四舍五入
            }
            if (total == 0.0)
                return "0%"
            var accuracy = num / total * 100
            if (accuracy > 100)
                accuracy = 100.0
            return df.format(accuracy) + "%"
        }

        override fun completed(task: BaseDownloadTask) {
            val savePath = task.targetFilePath
            AppUtils.installApk(this@VersionUpdateService, File(savePath))
            stopSelf()
        }

        override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
            isPause = true
            val pauseText = getString(R.string.version_update_pause)
            mBuilder?.setContentText(pauseText)
            notifyId()
        }

        override fun error(task: BaseDownloadTask, e: Throwable) {
            isError = true
            val errorText = getString(R.string.version_update_error)
            mBuilder?.setContentText(errorText)
            notifyId()
            if (isForce) execute()
        }
    }

    private fun notifyId() {
        notificationManager.notify(NOTIFICATION_ID, mBuilder?.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancel(NOTIFICATION_ID)
        FileDownloader.getImpl().pause(downloadListener)
        unregisterReceiver(receiver)
    }
}