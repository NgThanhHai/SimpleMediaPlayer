package com.example.simplemediaplayer.models


import android.text.format.DateUtils
import com.google.gson.annotations.SerializedName

data class Track (

    @SerializedName("err") val err : Int,
    @SerializedName("msg") val msg : String,
    @SerializedName("data") val data : TrackData,
    @SerializedName("timestamp") val timestamp : Any
)

data class TrackData (

    @SerializedName("id") val id : String,
    @SerializedName("name") val name : String,
    @SerializedName("title") val title : String,
    @SerializedName("code") val code : String,
    @SerializedName("content_owner") val content_owner : Int,
    @SerializedName("isoffical") val isoffical : Boolean,
    @SerializedName("isWorldWide") val isWorldWide : Boolean,
    @SerializedName("playlist_id") val playlist_id : String,
    @SerializedName("artists") val artists : List<TrackArtists>,
    @SerializedName("artists_names") val artists_names : String,
    @SerializedName("performer") val performer : String,
    @SerializedName("type") val type : String,
    @SerializedName("link") val link : String,
    @SerializedName("lyric") val lyric : String,
    @SerializedName("thumbnail") val thumbnail : String,
    @SerializedName("duration") val duration : Int,
    @SerializedName("source") val source : TrackSource,
    @SerializedName("album") val album : TrackAlbum,
    @SerializedName("artist") val artist : TrackArtist,
    @SerializedName("ads") val ads : Boolean,
    @SerializedName("is_vip") val is_vip : Boolean,
    @SerializedName("ip") val ip : String,
    var isSelected: Boolean = false) {
    val displayDuration: String
        get() {
            fun formatDuration(seconds: Long): String = if (seconds < 60) {
                seconds.toString()
            } else {
                DateUtils.formatElapsedTime(seconds)
            }

            return formatDuration(duration.toLong())
        }
}

data class TrackSource (
    @SerializedName("128") val _128 : String,
    @SerializedName("320") val _320 : String)

data class TrackArtists (
    @SerializedName("name") val name : String,
    @SerializedName("link") val link : String
)

data class TrackArtist (
    @SerializedName("id") val id : String,
    @SerializedName("name") val name : String,
    @SerializedName("link") val link : String,
    @SerializedName("cover") val cover : String,
    @SerializedName("thumbnail") val thumbnail : String
)

data class TrackAlbum (
    @SerializedName("id") val id : String,
    @SerializedName("link") val link : String,
    @SerializedName("title") val title : String,
    @SerializedName("name") val name : String,
    @SerializedName("isoffical") val isoffical : Boolean,
    @SerializedName("artists_names") val artists_names : String,
    @SerializedName("artists") val artists : List<TrackArtists>,
    @SerializedName("thumbnail") val thumbnail : String,
    @SerializedName("thumbnail_medium") val thumbnail_medium : String
)