package com.example.simplemediaplayer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplemediaplayer.models.Song
import com.example.simplemediaplayer.models.Track
import com.example.simplemediaplayer.models.TrackData
import com.example.simplemediaplayer.repositories.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel: ViewModel() {

    private var playlistRepository: PlaylistRepository = PlaylistRepository.getInstance()
    private var songList = MutableLiveData<List<Song>>()
    private var tracks = MutableLiveData<List<TrackData>>()
    private var currentSelectedTracks = MutableLiveData<TrackData>()
    private var isLoading = MutableLiveData(true)

    init {
        fetchSongList()
    }
    fun loadingLiveData(): LiveData<Boolean> = isLoading
    fun getSongListSource(): LiveData<List<TrackData>>{
        return tracks
    }

    fun getCurrentSelectedTracks() = currentSelectedTracks

    private fun fetchSongList() = viewModelScope.launch{
        val downloadingTracks : ArrayList<TrackData> = ArrayList()
        try {
            withContext(Dispatchers.IO) {
                songList.postValue(playlistRepository.getPlaylist())
            }
            songList.value?.forEach{ song ->
                downloadingTracks.add(playlistRepository.getTrack(song.code).data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            tracks.postValue(downloadingTracks)
            isLoading.postValue(false)
        }
    }

    fun onSelectTrack(track: TrackData) {
        currentSelectedTracks.value = track
    }
}