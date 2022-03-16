package com.example.simplemediaplayer.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplemediaplayer.models.Song
import com.example.simplemediaplayer.models.TrackData
import com.example.simplemediaplayer.repositories.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {

    var chosenTrack: MutableLiveData<Int> = MutableLiveData(-1)
    private var playlistRepository: PlaylistRepository = PlaylistRepository.getInstance()
    private var songList = MutableLiveData<List<Song>>()
    private var tracks = MutableLiveData<List<TrackData>>()
    var isLoading = MutableLiveData(true)


    init {
        fetchSongList()
    }

    fun getChosenTrackPosition(): LiveData<Int> = chosenTrack

    fun getSongList(): LiveData<List<Song>>
    {
        return songList
    }

    fun getSongListSource(): LiveData<List<TrackData>>{
        return tracks
    }

    private fun fetchSongList() = viewModelScope.launch(Dispatchers.Main){

        val downloadingTracks : ArrayList<TrackData> = ArrayList()
        try {

            val downloadPlaylistJob = this.async(Dispatchers.IO) {

                try {
                    songList.postValue(playlistRepository.getPlaylist())
                }
                catch (e:Exception)
                {
                    Log.e("exception", "${e.message}")
                }
            }
            downloadPlaylistJob.await()

            for (i in songList.value!!)
            {
                try {
                    downloadingTracks.add(playlistRepository.getTrack(i.code).data)
                    Log.d("link ", downloadingTracks[i.position-1].source._128 )
                }catch (e:Exception)
                {
                    Log.e("exception", "${e.message}")
                }
            }

        } catch (e: Exception) {
        } finally {
            tracks.postValue(downloadingTracks)
        }
        isLoading.postValue(false)
    }
}