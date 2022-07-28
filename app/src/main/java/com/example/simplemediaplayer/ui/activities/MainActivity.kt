package com.example.simplemediaplayer.ui.activities

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplemediaplayer.R
import com.example.simplemediaplayer.adapters.PlayListRecyclerViewAdapter
import com.example.simplemediaplayer.databinding.ActivityMainBinding
import com.example.simplemediaplayer.ext.formatDuration
import com.example.simplemediaplayer.models.TrackData
import com.example.simplemediaplayer.ui.dialogs.LoadingDialog
import com.example.simplemediaplayer.viewmodels.MainActivityViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var rvPlaylistAdapter: PlayListRecyclerViewAdapter
    private lateinit var runnable: Runnable
    private lateinit var loadingDialog: LoadingDialog
    private var currentTrack: TrackData? = null
    var mediaPlayer: MediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        loadingDialog = LoadingDialog()

        configMediaPlayer()
        initEvent()
        initBehaviour()
    }

    private fun initBehaviour() {
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
                binding.btnPlayPause.setBackgroundResource(R.drawable.ic_baseline_play_circle_outline_24)
            } else {
                mediaPlayer.start()
                binding.btnPlayPause.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24)
            }
        }

        binding.btnPrevious.setOnClickListener {
            rvPlaylistAdapter.playPrevious()
        }
        binding.btnNext.setOnClickListener {
            rvPlaylistAdapter.playNext()
        }

        binding.btnShare.setOnClickListener {
            shareTrack()
        }

        initPendingSeekbarBehaviour()
    }

    private fun shareTrack() {
        if (currentTrack?.link?.isNotEmpty() == true) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "https://zingmp3.vn" + currentTrack?.link.toString()
            )
            sendIntent.type = "text/plain"
            sendIntent.setPackage("com.facebook.orca")
            try {
                startActivity(sendIntent)
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Select a song first", Toast.LENGTH_SHORT).show()
        }

    }


    private fun configMediaPlayer() {
        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes
                    .Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setVolume(20F, 20F)
        }
    }

    private fun initEvent() {
        viewModel.getSongListSource().observe(this) {
            it?.let {
                rvPlaylistAdapter = PlayListRecyclerViewAdapter(it)
                binding.rvPlaylist.apply {
                    setItemViewCacheSize(200)
                    hasFixedSize()
                    layoutManager = LinearLayoutManager(
                        context,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = rvPlaylistAdapter
                }
                rvPlaylistAdapter.onItemClick = {
                    currentTrack = it
                    playSong(it)
                }
            }
        }

        viewModel.loadingLiveData().observe(this) {
            if (it) {
                loadingDialog.show(supportFragmentManager, "")
            } else {
                loadingDialog.dismiss()
            }
        }
    }


    private fun initPendingSeekbarBehaviour() {
        val handler = Handler()
        runnable = Runnable {
            try {
                binding.sbMusic.progress = mediaPlayer.currentPosition
                if (binding.sbMusic.progress + 1 >= binding.sbMusic.max) {
                    rvPlaylistAdapter.playNext()
                }
            } catch (ex: Exception) {
                Log.d("Exception in seekbar", ex.toString())
            }
            binding.tvCurrentDuration.text =
                formatDuration((binding.sbMusic.progress / 1000).toLong())
            handler.postDelayed(runnable, 1000)

        }
        handler.postDelayed(runnable, 1000)
    }


    private fun playSong(data: TrackData) {
        if (mediaPlayer.isPlaying) {
            clearMediaPlayer()
        }
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer.apply {
                setDataSource(data.source._128)
                prepareAsync()
                setOnPreparedListener {
                    start()
                    binding.apply {
                        btnPlayPause.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24)
                        data.apply {
                            tvSongTitle.text = name
                            tvSingerName.text = artists_names
                            tvMaxDuration.text = displayDuration
                        }
                        sbMusic.apply {
                            progress = 0
                            max = data.duration * 1000
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Log.d("Exception: ", ex.toString())
        }

    }

    private fun clearMediaPlayer() {
        mediaPlayer.apply {
            stop()
            release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
    }
}