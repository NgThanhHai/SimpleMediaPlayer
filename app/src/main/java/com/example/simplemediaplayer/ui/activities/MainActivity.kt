package com.example.simplemediaplayer.ui.activities

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplemediaplayer.R
import com.example.simplemediaplayer.adapters.PlayListRecyclerViewAdapter
import com.example.simplemediaplayer.databinding.ActivityMainBinding
import com.example.simplemediaplayer.models.TrackData
import com.example.simplemediaplayer.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var rvPlaylistAdapter: PlayListRecyclerViewAdapter
    private lateinit var runnable : Runnable
    lateinit var listTrack: List<TrackData>
    var mediaPlayer: MediaPlayer = MediaPlayer()
    var chosenTrack = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setVolume(20F,20F)

        viewModel.getSongListSource().observe(this) {
            it?.let {
                rvPlaylistAdapter = PlayListRecyclerViewAdapter(it)
                listTrack = it
                binding.rvPlaylist.setItemViewCacheSize(200)
                binding.rvPlaylist.hasFixedSize()
                binding.rvPlaylist.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                binding.rvPlaylist.adapter = rvPlaylistAdapter
                rvPlaylistAdapter.onItemClick = {

                    chosenTrack = it
                    playSong(it)
                }
            }
        }
        viewModel.isLoading.observe(this) {
            it?.let {
                if (it) {
                    binding.rvPlaylist.visibility = View.GONE
                    binding.loadingProgressbar.visibility = View.VISIBLE
                } else {
                    binding.rvPlaylist.visibility = View.VISIBLE
                    binding.loadingProgressbar.visibility = View.GONE
                }
            }
        }
        binding.sbMusic.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {

            var currentProgress = 0

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                currentProgress = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mediaPlayer.seekTo(currentProgress)
            }

        })
        binding.btnPlayPause.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                binding.btnPlayPause.setImageResource(
                    R.drawable.ic_baseline_play_circle_outline_24
                )
            } else {
                mediaPlayer.start()
                binding.btnPlayPause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
            }
        }

        binding.btnPrevious.setOnClickListener {
            playPrevious()
        }
        binding.btnNext.setOnClickListener {
            playNext()
        }

        pendingSeekbar()
    }
    private fun playNext(){
        if (chosenTrack != -1)
        {
            chosenTrack = (truncate(chosenTrack + 1))
            playSong(chosenTrack)
        }
    }

    private fun playPrevious(){
        if (chosenTrack != -1)
        {
            chosenTrack = (truncate(chosenTrack - 1))
            playSong(chosenTrack)
        }
    }

    private fun pendingSeekbar() {
        val handler = Handler()
        runnable = Runnable {
            try {
                binding.sbMusic.progress = mediaPlayer.currentPosition
                if(binding.sbMusic.progress + 1 >= binding.sbMusic.max)
                {
                    playNext()
                }
            } catch (ex: Exception) {
                    Log.d("Exception in seekbar", ex.toString())
            }
            binding.tvCurrentDuration.text =
                formatDuration((binding.sbMusic.progress / 1000).toLong())
            handler.postDelayed(runnable,1000)

        }
        handler.postDelayed(runnable,1000)
    }

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

    private fun truncate(value: Int): Int {
        return when {
            value >= listTrack.size -> {
                0
            }
            value < 0 -> {
                listTrack.size - 1
            }
            else -> {
                value
            }
        }
    }

    private fun playSong(position: Int) {
        if (position != -1) {

            if (mediaPlayer.isPlaying) {
                clearMediaPlayer()
            }
            try {
                mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(listTrack[position].source._128)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    mediaPlayer.start()
                    binding.btnPlayPause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
                    binding.tvSongTitle.text = listTrack[position].name
                    binding.tvSingerName.text = listTrack[position].artists_names
                    binding.tvMaxDuration.text = listTrack[position].displayDuration
                    binding.sbMusic.progress = 0
                    binding.sbMusic.max = listTrack[position].duration * 1000
                }
            }catch (ex: Exception)
            {
                Log.d("Exception: ", ex.toString())
            }
        }
    }

    private fun clearMediaPlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
    }
}