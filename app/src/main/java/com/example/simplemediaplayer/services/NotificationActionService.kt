package com.example.simplemediaplayer.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.simplemediaplayer.utils.Constants

class NotificationActionService: BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        val intent = Intent(Constants.INTENT_FILTER_ACTION).putExtra("actionName", p1!!.action)
        context!!.sendBroadcast(intent)
    }
}