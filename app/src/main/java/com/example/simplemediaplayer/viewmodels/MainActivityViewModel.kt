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
import kotlinx.coroutines.withContext

class MainActivityViewModel: ViewModel() {

    var chosenTrack: MutableLiveData<Int> = MutableLiveData(-1)
    private var playlistRepository: PlaylistRepository = PlaylistRepository.getInstance()
    private var songList = MutableLiveData<List<Song>>()
    private var tracks = MutableLiveData<List<TrackData>>()
    var isLoading = MutableLiveData(true)


    init {
        fetchSongList2()
    }
    fun loadingLiveData(): LiveData<Boolean> = isLoading
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
            isLoading.postValue(false)
        }
    }
    private fun fetchSongList2() = viewModelScope.launch{
        val downloadingTracks : ArrayList<TrackData> = ArrayList()
        try {
            withContext(Dispatchers.IO) {
                songList.postValue(playlistRepository.getPlaylist())
            }
            songList.value?.forEach{ song ->
                downloadingTracks.add(playlistRepository.getTrack(song.code).data)
            }
        } catch (e: Exception) {
        } finally {
            tracks.postValue(downloadingTracks)
            isLoading.postValue(false)
        }
    }
}