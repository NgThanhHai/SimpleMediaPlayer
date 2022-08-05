package com.example.simplemediaplayer.ui.activities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplemediaplayer.R
import com.example.simplemediaplayer.adapters.PlayListRecyclerViewAdapter
import com.example.simplemediaplayer.databinding.ActivityMainBinding
import com.example.simplemediaplayer.ext.formatDuration
import com.example.simplemediaplayer.interfaces.Playable
import com.example.simplemediaplayer.models.TrackData
import com.example.simplemediaplayer.services.OnClearFromSessionService
import com.example.simplemediaplayer.ui.dialogs.LoadingDialog
import com.example.simplemediaplayer.utils.CreateNotification
import com.example.simplemediaplayer.viewmodels.MainActivityViewModel


class MainActivity : AppCompatActivity(), Playable {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private var rvPlaylistAdapter = PlayListRecyclerViewAdapter()
    private lateinit var runnable: Runnable
    private lateinit var loadingDialog: LoadingDialog
    private var currentTrack: TrackData? = null
    private var pos : Int = -1
    private var size: Int = -1
    var mediaPlayer: MediaPlayer = MediaPlayer()
    lateinit var notificationManager: NotificationManager
    private var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            var action: String = p1!!.extras!!.getString("actionName").toString()
            when(action) {
                CreateNotification.ACTION_PREVIOUS -> {
                    onTrackPrevious()
                }
                CreateNotification.ACTION_NEXT -> {
                    onTrackNext()
                }
                CreateNotification.ACTION_PLAY -> {
                    if (mediaPlayer.isPlaying) {
                        onTrackPause()
                    } else {
                        onTrackPlay()
                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        loadingDialog = LoadingDialog()

        configMediaPlayer()
        createChannel()
        initListSong()
        initEvent()
        initBehaviour()
    }

    private fun createChannel() {
        val channel = NotificationChannel(CreateNotification.CHANNEL_ID, "Music Player", NotificationManager.IMPORTANCE_LOW)
        notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        registerReceiver(broadcastReceiver, IntentFilter("Tracks"))
        startService(Intent(applicationContext, OnClearFromSessionService::class.java))
    }

    private fun initListSong() {
        binding.rvPlaylist.apply {
            setItemViewCacheSize(200)
            hasFixedSize()
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
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
                onTrackPause()
            } else {
                onTrackPlay()
            }
        }

        rvPlaylistAdapter.onItemClick = { data, p, s ->
            currentTrack = data
            pos = p
            size = s
            playSong(data, pos, size)
        }

        binding.btnPrevious.setOnClickListener {
            onTrackPrevious()
        }
        binding.btnNext.setOnClickListener {
            onTrackNext()
        }

        binding.btnShare.setOnClickListener {
            onTrackShare()
        }

        initPendingSeekbarBehaviour()
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
                rvPlaylistAdapter.addAll(it)
                binding.rvPlaylist.adapter = rvPlaylistAdapter
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

    private fun playSong(data: TrackData, pos: Int, size: Int) {
        CreateNotification.createNotification(this, data, R.drawable.ic_baseline_pause_circle_outline_24, position = pos, size)
        if(data.id.isNotEmpty()) {
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
    }

    private fun clearMediaPlayer() {
        mediaPlayer.apply {
            stop()
            release()
        }
    }


    override fun onStop() {
        super.onStop()
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn = pm.isScreenOn
        if(!isScreenOn && mediaPlayer.isPlaying) {
            binding.btnPlayPause.performClick()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
        notificationManager.cancelAll()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onTrackPrevious() {
        rvPlaylistAdapter.playPrevious()
    }

    override fun onTrackPause() {
        mediaPlayer.pause()
        binding.btnPlayPause.setBackgroundResource(R.drawable.ic_baseline_play_circle_outline_24)
        CreateNotification.createNotification(this, currentTrack!!, R.drawable.ic_baseline_play_circle_outline_24, position = pos, size)
    }

    override fun onTrackPlay() {
        mediaPlayer.start()
        binding.btnPlayPause.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24)
        CreateNotification.createNotification(this, currentTrack!!, R.drawable.ic_baseline_pause_circle_outline_24, position = pos, size)
    }

    override fun onTrackNext() {
        rvPlaylistAdapter.playNext()
    }

    override fun onTrackShare() {
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

}