package com.example.simplemediaplayer.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.simplemediaplayer.R
import com.example.simplemediaplayer.models.TrackData
import com.example.simplemediaplayer.services.NotificationActionService
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class CreateNotification {

    companion object{
        var CHANNEL_ID = "channel1"
        var ACTION_PREVIOUS = "action previous"
        var ACTION_PLAY = "action play"
        var ACTION_NEXT = "action next"
        private var notification = Notification()

        @SuppressLint("UnspecifiedImmutableFlag")
        fun createNotification(context: Context, data: TrackData, playPauseButton: Int, position: Int, size: Int){
            val notificationManagerCompat: NotificationManagerCompat = NotificationManagerCompat.from(context)
            var icon: Bitmap? = null
            val pendingIntentPrevious : PendingIntent?
            val drwPrevious: Int
            val intentPlay = Intent(context, NotificationActionService::class.java).setAction(ACTION_PLAY)
            val pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT)

            val pendingIntentNext : PendingIntent?
            val drwNext: Int

            if(position == 0){
                pendingIntentPrevious = null
                drwPrevious = 0
            }else {
                val intentPrevious = Intent(context, NotificationActionService::class.java).setAction(ACTION_PREVIOUS)
                pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT)
                drwPrevious = R.drawable.ic_previous_24

            }

            if(position == size){
                pendingIntentNext = null
                drwNext = 0
            }else {
                val intentNext = Intent(context, NotificationActionService::class.java).setAction(
                    ACTION_NEXT)
                pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT)
                drwNext = R.drawable.ic_next_24
            }

//            GlobalScope.launch {
//                var job = CoroutineScope(this.coroutineContext).async {
//                    icon = Picasso.get().load(data.thumbnail).get()
//                }
//                job.await()
//                notification = NotificationCompat.Builder(context, CHANNEL_ID)
//                    .setSmallIcon(R.drawable.ic_baseline_shuffle_24)
//                    .setLargeIcon(icon)
//                    .setContentTitle(data.title)
//                    .setContentText(data.artists_names)
//                    .addAction(drwPrevious, "Previous", pendingIntentPrevious)
//                    .addAction(playPauseButton, "Play", pendingIntentPlay)
//                    .addAction(drwNext, "Next", pendingIntentNext)
//                    .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2))
//                    .setShowWhen(false)
//                    .setOnlyAlertOnce(false)
//                    .setPriority(NotificationCompat.PRIORITY_LOW)
//                    .build()
//                notificationManagerCompat.notify(1, notification)
//            }

            GlobalScope.launch {
                icon = Picasso.get().load(data.thumbnail).get()
                withContext(Dispatchers.Main) {
                    notification = NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_music_2)
                        .setLargeIcon(icon)
                        .setContentTitle(data.title)
                        .setContentText(data.artists_names)
                        .addAction(drwPrevious, "Previous", pendingIntentPrevious)
                        .addAction(playPauseButton, "Play", pendingIntentPlay)
                        .addAction(drwNext, "Next", pendingIntentNext)
                        .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2))
                        .setShowWhen(false)
                        .setOnlyAlertOnce(false)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .build()
                    notificationManagerCompat.notify(1, notification)
                }

            }

        }

    }
}