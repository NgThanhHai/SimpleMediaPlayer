package com.example.simplemediaplayer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplemediaplayer.R
import com.example.simplemediaplayer.databinding.LayoutListCellBinding
import com.example.simplemediaplayer.models.TrackData
import com.squareup.picasso.Picasso

class PlayListRecyclerViewAdapter(var playlist: List<TrackData>): RecyclerView.Adapter<PlayListRecyclerViewAdapter.ViewHolder>() {

    var onItemClick: ((TrackData) -> Unit)? = null
    var chosenTrack = -1

    inner class ViewHolder(private var binding: LayoutListCellBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TrackData) {
            binding.track = item
            Picasso.get().load(item.thumbnail).into(binding.ivMiniSongPoster)
            if(item.isSelected) {
                binding.itemHOFContainer.setBackgroundResource(R.drawable.list_song_item_background_on)
            }else {
                binding.itemHOFContainer.setBackgroundResource(R.drawable.list_song_item_background_off)
            }
            binding.root.setOnClickListener {

                unselectedAll()
                notifyItemChanged(chosenTrack)
                chosenTrack = adapterPosition
                playlist[chosenTrack].isSelected = true
                notifyItemChanged(chosenTrack)
                onItemClick?.invoke(playlist[chosenTrack])
            }
        }

    }

    fun playNext() {
        unselectedAll()
        notifyItemChanged(chosenTrack)
        chosenTrack = truncate(chosenTrack + 1)
        playlist[chosenTrack].isSelected = true
        notifyItemChanged(chosenTrack)
        onItemClick?.invoke(playlist[chosenTrack])
    }

    fun playPrevious() {
        unselectedAll()
        notifyItemChanged(chosenTrack)
        chosenTrack = truncate(chosenTrack -1)
        playlist[chosenTrack].isSelected = true
        notifyItemChanged(chosenTrack)
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
        val layoutListCellBinding: LayoutListCellBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_list_cell, parent, false)
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