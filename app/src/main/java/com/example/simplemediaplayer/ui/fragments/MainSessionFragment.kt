package com.example.simplemediaplayer.ui.fragments

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.example.simplemediaplayer.R
import com.example.simplemediaplayer.adapters.PlayListRecyclerViewAdapter
import com.example.simplemediaplayer.databinding.FragmentMainSessionBinding
import com.example.simplemediaplayer.ext.formatDuration
import com.example.simplemediaplayer.interfaces.Playable
import com.example.simplemediaplayer.models.TrackData
import com.example.simplemediaplayer.ui.activities.MainActivity
import com.example.simplemediaplayer.ui.dialogs.LoadingDialog
import com.example.simplemediaplayer.utils.Constants
import com.example.simplemediaplayer.utils.CreateNotification
import com.squareup.picasso.Picasso


class MainSessionFragment : Fragment(), Playable {

    private lateinit var binding: FragmentMainSessionBinding
    private var rvPlaylistAdapter = PlayListRecyclerViewAdapter()
    private lateinit var rvLayoutManager: LinearLayoutManager
    private var smoothScroller: SmoothScroller? = null
    private lateinit var runnable: Runnable
    private lateinit var loadingDialog: LoadingDialog
    private var currentTrack: TrackData? = null
    var mediaPlayer: MediaPlayer = MediaPlayer()
    private lateinit var notificationManager: NotificationManager
    private var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            when(p1!!.extras!!.getString("actionName").toString()) {
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
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainSessionBinding.inflate(layoutInflater, container, false)
        loadingDialog = LoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configMediaPlayer()
        createChannel()
        initListSong()
        initEvent()
        initBehaviour()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun createChannel() {
        val channel = NotificationChannel(CreateNotification.CHANNEL_ID, "Music Player", NotificationManager.IMPORTANCE_LOW)
        notificationManager = requireContext().getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        requireContext().registerReceiver(broadcastReceiver, IntentFilter(Constants.INTENT_FILTER_ACTION))
        CreateNotification.initMediaSession(requireContext())
    }

    private fun initListSong() {
        binding.ivMainBackdrop.clipToOutline = true
        binding.rvPlaylist.apply {
            setItemViewCacheSize(200)
            hasFixedSize()
            rvLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            layoutManager = rvLayoutManager
        }
        smoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
    }

    private fun initBehaviour() {
        binding.sbSongProgress.setOnSeekBarChangeListener(object :
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

        rvPlaylistAdapter.onItemClick = { data ->
            currentTrack = data
            playSong(data)
            if(rvPlaylistAdapter.nextTrackOnQueue > 0) {
                smoothScroller?.let {
                    it.targetPosition = rvPlaylistAdapter.nextTrackOnQueue
                    rvLayoutManager.startSmoothScroll(it)
                }
            }
        }

        binding.btnPrevious.setOnClickListener {
            onTrackPrevious()
        }
        binding.btnNext.setOnClickListener {
            onTrackNext()
        }

//        binding.btnShare.setOnClickListener {
//            onTrackShare()
//        }

        binding.tvMore.setOnClickListener {
            (requireActivity() as MainActivity).openFullPlaylistFragment()
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
        (requireActivity() as MainActivity).viewModel.getSongListSource().observe(viewLifecycleOwner) {
            it?.let {
                rvPlaylistAdapter.addAll(it)
                binding.rvPlaylist.adapter = rvPlaylistAdapter
            }
        }

        (requireActivity() as MainActivity).viewModel.loadingLiveData().observe(viewLifecycleOwner) {
            if (it) {
                loadingDialog.show(parentFragmentManager, "")
            } else {
                loadingDialog.dismiss()
            }
        }
    }


    private fun initPendingSeekbarBehaviour() {
        val handler = Handler()
        runnable = Runnable {
            try {
                binding.sbSongProgress.progress = mediaPlayer.currentPosition
                if (binding.sbSongProgress.progress + 1 >= binding.sbSongProgress.max) {
                    rvPlaylistAdapter.playNext()
                }
            } catch (ex: Exception) {
                Log.d("Exception in seekbar", ex.toString())
            }
            binding.tvCurrentDuration.text =
                formatDuration((binding.sbSongProgress.progress / 1000).toLong())
            handler.postDelayed(runnable, 1000)

        }
        handler.postDelayed(runnable, 1000)
    }



    private fun playSong(data: TrackData) {
        CreateNotification.createNotification(requireContext(), data, R.drawable.ic_baseline_pause_circle_outline_24)
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
                            btnPlayPause.setBackgroundResource(R.drawable.baseline_pause_circle_filled_24)
                            data.apply {
                                tvSongTitle.text = name
                                tvSingerName.text = artists_names
                                tvMaxDuration.text = displayDuration
                                Picasso.get().load(thumbnail).fit().into(binding.ivMainBackdrop)
                            }
                            sbSongProgress.apply {
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

    override fun onTrackPrevious() {
        rvPlaylistAdapter.playPrevious()
    }

    override fun onTrackPause() {
        currentTrack?.let {
            mediaPlayer.pause()
            binding.btnPlayPause.setBackgroundResource(R.drawable.baseline_play_circle_filled_24)
            CreateNotification.createNotification(requireContext(), it, R.drawable.ic_baseline_play_circle_outline_24)
        }
    }

    override fun onTrackPlay() {
        currentTrack?.let {
            mediaPlayer.start()
            binding.btnPlayPause.setBackgroundResource(R.drawable.baseline_pause_circle_filled_24)
            CreateNotification.createNotification(requireContext(), it, R.drawable.ic_baseline_pause_circle_outline_24)
        }
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
            Toast.makeText(requireContext(), "Select a song first", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onStop() {
        super.onStop()
        val pm = requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn = pm.isInteractive
        if(!isScreenOn && mediaPlayer.isPlaying) {
            binding.btnPlayPause.performClick()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
        notificationManager.cancelAll()
        requireContext().unregisterReceiver(broadcastReceiver)
    }
}