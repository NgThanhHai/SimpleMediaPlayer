package com.example.simplemediaplayer.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.session.MediaSessionManager
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.simplemediaplayer.R
import com.example.simplemediaplayer.models.TrackData
import com.example.simplemediaplayer.services.NotificationActionService
import com.example.simplemediaplayer.ui.activities.MainActivity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*


object CreateNotification {
    private val internalScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    var CHANNEL_ID = "channel1"
    var ACTION_PREVIOUS = "action previous"
    var ACTION_PLAY = "action play"
    var ACTION_NEXT = "action next"
    private var notification = Notification()
    private var mediaSessionManager: MediaSessionManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null
    @SuppressLint("UnspecifiedImmutableFlag")
    fun createNotification(context: Context, data: TrackData, playPauseButton: Int) {
        val notificationManagerCompat: NotificationManagerCompat = NotificationManagerCompat.from(context)
        val intent = Intent(context, NotificationActionService::class.java)
        val intentPlay = intent.setAction(ACTION_PLAY)
        val pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_IMMUTABLE)
        val intentPrevious = intent.setAction(ACTION_PREVIOUS)
        val pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, PendingIntent.FLAG_IMMUTABLE)
        val drwPrevious: Int = R.drawable.ic_previous_24
        val intentNext = intent.setAction(ACTION_NEXT)
        val pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_IMMUTABLE)
        val drwNext: Int = R.drawable.ic_next_24

        internalScope.launch {
            val icon = Picasso.get().load(data.thumbnail).get()
            withContext(Dispatchers.Main) {
                notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music_2)
                    .setLargeIcon(icon)
                    .setContentTitle(data.title)
                    .setContentText(data.artists_names)
                    .addAction(drwPrevious, "Previous", pendingIntentPrevious)
                    .addAction(playPauseButton, "Play", pendingIntentPlay)
                    .addAction(drwNext, "Next", pendingIntentNext)
                    .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession?.sessionToken).setShowActionsInCompactView(0, 1, 2))
                    .setShowWhen(false)
                    .setOnlyAlertOnce(false)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build()
                notificationManagerCompat.notify(1, notification)
            }

        }
    }
    @Throws(RemoteException::class)
    fun initMediaSession(context: Context) {
        if (mediaSessionManager != null) return
        mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager?
        mediaSession = MediaSessionCompat(context, "AudioPlayer")
        transportControls = mediaSession?.controller?.transportControls
        mediaSession?.isActive = true
        mediaSession?.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
    }

}