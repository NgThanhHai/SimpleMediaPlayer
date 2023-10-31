package com.example.simplemediaplayer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemediaplayer.databinding.LayoutListCellBinding
import com.example.simplemediaplayer.models.TrackData
import com.squareup.picasso.Picasso

class PlayListRecyclerViewAdapter(var playlist: MutableList<TrackData> = mutableListOf()): RecyclerView.Adapter<PlayListRecyclerViewAdapter.ViewHolder>() {

    var onItemClick: ((TrackData) -> Unit)? = null
    var chosenTrack = -1
    var nextTrackOnQueue = 0

    fun addAll(lst: List<TrackData>){
        playlist.clear()
        playlist.addAll(lst)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private var binding: LayoutListCellBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TrackData) {
            binding.tvSongNamePL.text = item.title
            binding.tvArtistNamePL.text = item.artist.name
            binding.tvDuration.text = item.displayDuration
            binding.ivMiniSongPoster.clipToOutline = true
            Picasso.get().load(item.thumbnail).fit().into(binding.ivMiniSongPoster)
            binding.root.setOnClickListener {
                chosenTrack = adapterPosition
                nextTrackOnQueue = truncate(chosenTrack + 1)
                onItemClick?.invoke(playlist[chosenTrack])
            }
        }

    }

    fun playNext() {
        chosenTrack = truncate(chosenTrack + 1)
        nextTrackOnQueue = truncate(chosenTrack + 1)
        onItemClick?.invoke(playlist[chosenTrack])
    }

    fun playPrevious() {
        chosenTrack = truncate(chosenTrack - 1)
        nextTrackOnQueue = truncate(chosenTrack - 1)
        onItemClick?.invoke(playlist[chosenTrack])
    }

    private fun truncate(value: Int): Int {
        return when {
            value >= playlist.size -> {
                0
            }
            value < 0 -> {
                playlist.size - 1
            }
            else -> {
                value
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutListCellBinding: LayoutListCellBinding = LayoutListCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(layoutListCellBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder).bind(playlist[position])
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    fun unselectedAll() {
        for (element in playlist) {
            element.isSelected = false
        }
    }

}