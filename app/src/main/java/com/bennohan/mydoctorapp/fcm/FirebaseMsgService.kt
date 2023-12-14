package com.bennohan.mydoctorapp.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.ui.detailDoctor.DetailDoctorActivity
import com.bennohan.mydoctorapp.ui.home.NavigationActivity
import com.crocodic.core.helper.DateTimeHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class FirebaseMsgService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("firebase-token", token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val context: Context = applicationContext

        Log.d("fcmServis", "messageData:${message.data}")
//        Log.d("fcmServis", "${message.data["user_id"]}")
        Log.d(
            "firebase_receive_message_title",
            "firebase_receive_message_title: ${message.data["title"]}"
        )
        Log.d(
            "firebase_receive_message_reservation_id",
            "firebase_receive_message_reservation_id: ${message.data["reservation_id"]}"
        )
        Timber.d("firebase_receive_message_title : ${message.data["title"]}")
        Timber.d("firebase_receive_message_message : ${message.data["message"]}")


        showNotification(
            context,
            message.data["title"] ?: return,
            message.data["message"] ?: return,
            message.data["reservation_id"] ?: return
            //title mengambil titlenya, body itu messagenya
        )


    }

}

private fun sendRegistrationToServer(token: String?) {
    //implement this method to send token to your app server.
    //untuk mengirim device tokennya, Dipindah Ke Home
}

//todo: untuk edit notifikasinya, notifasi manager sudah ada di android
fun showNotification(context: Context, title: String, message: String , reservationId : String) {
    //todo:Notification Manager
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    //todo: Notification for Oreo >
    // untuk android 13 meminta notifikasi untuk notifikasi wajib untuk android 13 keatas
    // untuk menyeting notifikasinya
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "CHANNEL_ID", //untuk android 13
            "My Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Mhm"
        channel.enableLights(true)
        channel.enableVibration(true)
        notificationManager.createNotificationChannel(channel)
    }


    //todo: untuk edit titile, masage, logo
    // todo:Builder
    val homeIntent = Intent(context, NavigationActivity::class.java)
    val detailIntent = Intent(context, DetailDoctorActivity::class.java).apply {
//        putExtra(Const.DOCTOR.ID_DOCTOR, userId.toInt())
//        Log.d("cek Id", "cek Id : $userId")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }


    var resultPendingIntent: PendingIntent? =
        PendingIntent.getActivity(
            context, 1, detailIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        )

    val stackBuilder = TaskStackBuilder.create(context)
//    stackBuilder.addNextIntent(homeIntent)
    stackBuilder.addNextIntent(detailIntent)
    resultPendingIntent = stackBuilder.getPendingIntent(
        1,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(context, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_baseline_person_black)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(resultPendingIntent)
        .setAutoCancel(true)

    // todo:Show Notification
    val notificationId = DateTimeHelper().createAtLong().toInt()
    notificationManager.notify(notificationId, builder.build())
}
