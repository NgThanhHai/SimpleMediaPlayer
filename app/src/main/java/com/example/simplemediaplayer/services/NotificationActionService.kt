package com.example.simplemediaplayer.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationActionService: BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        val intent = Intent("Tracks").putExtra("actionName", p1!!.action)
        context!!.sendBroadcast(intent)
    }
}