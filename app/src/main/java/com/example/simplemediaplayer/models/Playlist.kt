package com.example.simplemediaplayer.models


import android.text.format.DateUtils
import com.google.gson.annotations.SerializedName

data class Playlist (

    @SerializedName("err") val err : Int,
    @SerializedName("msg") val msg : String,
    @SerializedName("data") val data : Data,
    @SerializedName("timestamp") val timestamp : Any
)

data class Data (

    @SerializedName("song") val song : List<Song>,
    @SerializedName("customied") val customied : List<String>,
    @SerializedName("peak_score") val peak_score : Int,
    @SerializedName("songHis") val songHis : SongHis
)

data class Song (

    @SerializedName("id") val id : String,
    @SerializedName("name") val name : String,
    @SerializedName("title") val title : String,
    @SerializedName("code") val code : String,
    @SerializedName("content_owner") val content_owner : Int,
    @SerializedName("isoffical") val isoffical : Boolean,
    @SerializedName("isWorldWide") val isWorldWide : Boolean,
    @SerializedName("playlist_id") val playlist_id : String,
    @SerializedName("artists") val artists : List<Artists>,
    @SerializedName("artists_names") val artists_names : String,
    @SerializedName("performer") val performer : String,
    @SerializedName("type") val type : String,
    @SerializedName("link") val link : String,
    @SerializedName("lyric") val lyric : String,
    @SerializedName("thumbnail") val thumbnail : String,
    @SerializedName("mv_link") val mv_link : String,
    @SerializedName("duration") val duration : Int,
    @SerializedName("total") val total : Int,
    @SerializedName("rank_num") val rank_num : String,
    @SerializedName("rank_status") val rank_status : String,
    @SerializedName("artist") val artist : Artist,
    @SerializedName("position") val position : Int,
    @SerializedName("order") val order : Int,
    @SerializedName("album") val album : Album
)
{
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

data class SongHis (

    @SerializedName("min_score") val min_score : Int,
    @SerializedName("max_score") val max_score : Any,
    @SerializedName("from") val from : Any,
    @SerializedName("interval") val interval : Int,
    @SerializedName("data") val data : Data,
    @SerializedName("score") val score : Score,
    @SerializedName("total_score") val total_score : Int
)

data class Artist (

    @SerializedName("id") val id : String,
    @SerializedName("name") val name : String,
    @SerializedName("link") val link : String,
    @SerializedName("cover") val cover : String,
    @SerializedName("thumbnail") val thumbnail : String
)

data class Artists (

    @SerializedName("name") val name : String,
    @SerializedName("link") val link : String
)

data class Album (

    @SerializedName("id") val id : String,
    @SerializedName("link") val link : String,
    @SerializedName("title") val title : String,
    @SerializedName("name") val name : String,
    @SerializedName("isoffical") val isoffical : Boolean,
    @SerializedName("artists_names") val artists_names : String,
    @SerializedName("artists") val artists : List<Artists>,
    @SerializedName("thumbnail") val thumbnail : String,
    @SerializedName("thumbnail_medium") val thumbnail_medium : String
)


data class Score (

    @SerializedName("ZU0E8DO0") val zU0E8DO0 : ZU0E8DO0,
    @SerializedName("ZU0WU9EF") val zU0WU9EF : ZU0WU9EF,
    @SerializedName("ZU0C6UO8") val zU0C6UO8 : ZU0C6UO8
)

data class ZU0C6UO8 (

    @SerializedName("total_score") val total_score : Int,
    @SerializedName("total_peak_score") val total_peak_score : Int,
    @SerializedName("total_score_realtime") val total_score_realtime : Int
)

data class ZU0E8DO0 (

    @SerializedName("total_score") val total_score : Int,
    @SerializedName("total_peak_score") val total_peak_score : Int,
    @SerializedName("total_score_realtime") val total_score_realtime : Int
)


data class ZU0WU9EF (

    @SerializedName("total_score") val total_score : Int,
    @SerializedName("total_peak_score") val total_peak_score : Int,
    @SerializedName("total_score_realtime") val total_score_realtime : Int
)