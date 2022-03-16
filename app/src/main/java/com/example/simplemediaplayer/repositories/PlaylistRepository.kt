package com.example.simplemediaplayer.repositories

import com.example.simplemediaplayer.api.ApiFactory
import com.example.simplemediaplayer.models.Song
import com.example.simplemediaplayer.models.Track

class PlaylistRepository {

    companion object {
        private var INSTANCE: PlaylistRepository? = null
        fun getInstance() = INSTANCE?: PlaylistRepository().also{
            INSTANCE = it
        }
    }

    suspend fun getPlaylist()  : List<Song>  {
        return ApiFactory.instance.getZingChartPlaylist().data.song
    }

    suspend fun getTrack(code: String): Track
    {
        return ApiFactory.instance.getTrack(code)
    }
}

