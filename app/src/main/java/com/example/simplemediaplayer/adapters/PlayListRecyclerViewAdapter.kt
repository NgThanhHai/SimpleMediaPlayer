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

    var onItemClick: ((Int) -> Unit)? = null

    inner class ViewHolder(itemView: LayoutListCellBinding): RecyclerView.ViewHolder(itemView.root) {
        var layoutListCellBinding: LayoutListCellBinding = itemView

        private val ivPoster = itemView.ivMiniSongPoster

        fun setUpPoster(trackData: TrackData) {
            if (trackData.thumbnail.isNullOrBlank()) {
                //Picasso.get().load(R.drawable.ic_default_male_avatar).into(ivAvatar)
                return
            }else{
                Picasso.get().load(trackData.thumbnail).into(ivPoster)
            }
        }

        init {
            itemView.root.setOnClickListener {
                onItemClick?.invoke(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutListCellBinding: LayoutListCellBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_list_cell, parent, false)

        return ViewHolder(layoutListCellBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = playlist[position]
        holder.layoutListCellBinding.track = track
        holder.setUpPoster(track)
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

}