package com.example.simplemediaplayer.api

import com.example.simplemediaplayer.models.Playlist
import com.example.simplemediaplayer.models.Track
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {


    @GET("chart-realtime?songId=0&videoId=0&albumId=0&chart=song&time=-1")
    suspend fun getZingChartPlaylist(): Playlist


    @GET("media/get-source?type=audio")
    suspend fun getTrack(@Query("key") key: String): Track

}