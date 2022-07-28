package com.example.simplemediaplayer.ext

import android.text.format.DateUtils

fun formatDuration(seconds: Long): String = if (seconds < 60) {
    if (seconds == 0L) {
        "00:00"
    } else {
        if (seconds < 10) {
            "00:0$seconds"
        } else {
            "00:$seconds"
        }
    }

} else {
    DateUtils.formatElapsedTime(seconds)
}