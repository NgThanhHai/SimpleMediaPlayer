package com.example.simplemediaplayer.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.example.simplemediaplayer.R
import com.example.simplemediaplayer.adapters.PlayListRecyclerViewAdapter
import com.example.simplemediaplayer.databinding.FragmentFullPlaylistBinding
import com.example.simplemediaplayer.ui.activities.MainActivity


class FullPlaylistFragment : Fragment() {

    private lateinit var binding: FragmentFullPlaylistBinding
    private lateinit var rvLayoutManager: LinearLayoutManager
    private var rvPlaylistAdapter = PlayListRecyclerViewAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFullPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListSong()
        (requireActivity() as MainActivity).viewModel.getSongListSource().observe(viewLifecycleOwner) {
            it?.let {
                rvPlaylistAdapter.addAll(it)
                binding.rvPlaylist.adapter = rvPlaylistAdapter
            }
        }

    }

    private fun initListSong() {
        binding.rvPlaylist.apply {
            setItemViewCacheSize(200)
            hasFixedSize()
            rvLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            layoutManager = rvLayoutManager
        }
    }
}