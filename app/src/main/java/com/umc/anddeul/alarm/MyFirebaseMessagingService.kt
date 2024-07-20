package com.umc.anddeul.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.umc.anddeul.MainActivity
import com.umc.anddeul.checklist.ChecklistFragment
import com.umc.anddeul.home.HomeFragment
import com.umc.anddeul.postbox.PostboxFragment

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "myFMS"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // 데이터 페이로드가 있는지 확인
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            val type = remoteMessage.data["type"]
            val message = remoteMessage.data["message"]
            sendNotification(type, message)
        }

        // 알림 페이로드가 있는지 확인
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        // 새로운 토큰을 서버로 전송하는 코드 필요 시 추가
    }

    private fun sendNotification(type: String?, messageBody: String?) {
        val intent = when (type) {
            "home" -> Intent(this, HomeFragment::class.java)
            "checklist" -> Intent(this, ChecklistFragment::class.java)
            "postbox" -> Intent(this, PostboxFragment::class.java)
            else -> Intent(this, MainActivity::class.java)
        }.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelId = "default_channel_id"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("안뜰")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android O 이상에서는 채널이 필요합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}
